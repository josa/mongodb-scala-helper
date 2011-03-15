package br.com.gfuture.mongodbhelper

import annotations.{AssociationType, DocElement, CascadeType, Reference}
import com.mongodb.casbah.commons.MongoDBObject
import java.lang.reflect.Field
import mongodb.MongoProvider
import org.bson.types.ObjectId
import br.com.gfuture.mongodbhelper.reflect.ReflectUtil
import org.slf4j.LoggerFactory
import com.mongodb.{WriteResult, DBCollection, DBObject}

abstract class Document(val documentClass: Class[_ <: Document]) {

  protected lazy val logger = LoggerFactory.getLogger(getClass)

  private var objectId: ObjectId = null

  def save {

    if (logger.isDebugEnabled)
      logger.debug("saving " + getClass.getSimpleName)

    try {

      //MongoDocumentHelper.callPrePersist(asInstanceOf[Document[T]])

      val mongoObject: DBObject = DocumentTools.toDBObject(this)

      if (logger.isDebugEnabled)
        logger.debug(mongoObject.toString)

      getObjectId match {
        case objectId: ObjectId =>
          val uniqueMongoObject = DocumentTools.toOBObject(this.getObjectId)
          update(uniqueMongoObject, mongoObject, getClass)
        case _ =>
          save(mongoObject, getClass)
          setObjectId(mongoObject.get("_id").asInstanceOf[org.bson.types.ObjectId])
      }

      //MongoDocumentHelper.callPosPersist(this)

      if (logger.isDebugEnabled)
        logger.debug("sucess")

    } catch {
      case e: Exception =>
        logger.error("erro ao salvar " + getClass.getSimpleName, e)
        throw e
    }
  }


  /**
   * Exclui a entidade do mongodb
   *
   * @param a entidade
   * @return voids
   */
  def delete {
    val writeResult: WriteResult = MongoProvider.getCollection(documentClass).remove(DocumentTools.toOBObject(getObjectId))
    if (writeResult.getError != null)
      throw new PersistenceException(writeResult.getError)
  }

  /**Salva o dbObject no mongodb
   */
  private def save(dbObject: DBObject, documentClass: Class[_]) {
    val bCollection: DBCollection = MongoProvider.getCollection(documentClass)
    val writeResult: WriteResult = bCollection.save(dbObject)
    if (writeResult.getError != null)
      throw new PersistenceException(writeResult.getError)
  }

  /**Atualiza o dbObject no mongodb
   *
   * @param o unique dbObject
   * @param o dbObject a ser salvo
   */
  private def update(uniqueDbObject: DBObject, dbObject: DBObject, documentClass: Class[_]) {
    val collection: DBCollection = MongoProvider.getCollection(documentClass)
    val writeResult: WriteResult = collection.update(uniqueDbObject, dbObject)
    if (writeResult.getError != null)
      throw new PersistenceException(writeResult.getError)
  }

  override def equals(that: Any) = that match {
    case other: Document => other.getClass == getClass && other.getObjectId.equals(getObjectId)
    case _ => false
  }

  override def toString = {
    getClass.getSimpleName + ", " + DocumentTools.toDBObject(this).toString
  }

  def getObjectId = this.objectId

  def setObjectId(objectId: ObjectId) {
    this.objectId = objectId
  }

}

object DocumentTools {

  protected lazy val logger = LoggerFactory.getLogger(DocumentTools.getClass)

  /**Converte o objeto em um objeto de persistencia do mongo
   *
   * @param document, a entidade a ser convertida
   * @return uma instancia da classe com.mongodb.DBObject
   */
  def toDBObject(document: Document): DBObject = {

    val builder = MongoDBObject.newBuilder

    def processAnnotationsAndGenerateElement(field: Field): (String, AnyRef) = {
      field.getAnnotation(classOf[Reference]) match {
        case refAnnotation: Reference =>

          val documentReference = field.get(document).asInstanceOf[Document]
          if (!refAnnotation.cascade.equals(CascadeType.SAVE) && documentReference.getObjectId == null)
            throw new MappingException(field.getName + ": objectId not exists")

          //TODO - FIXEME
          //if (refAnnotation.cascade.equals(CascadeType.SAVE))
          //documentReference.save

          field.getName -> documentReference.getObjectId

        case null =>
          field.getName -> field.get(document)
      }
    }

    ReflectUtil.loadFieldsRecursively(document.documentClass).foreach {
      field =>
        field.setAccessible(true)
        if (field.isAnnotationPresent(classOf[DocElement]) && field.get(document) != null)
          builder += processAnnotationsAndGenerateElement(field)
        else {
          if (logger.isDebugEnabled) {
            if (!field.isAnnotationPresent(classOf[DocElement]))
              logger.debug("toObject[field excluded(%s), @DocElement not found]" format (field.getName))
            if (field.get(document) != null)
              logger.debug("toObject[field excluded(%s), is null]" format (field.getName))
          }
        }
    }

    builder += "_id" -> document.getObjectId
    builder.result
  }

  /**
   * Converte o _id em um objeto de persistencia do mongo
   *
   * @param _id, instancia da classe org.bson.types.ObjectId
   * @return uma instancia da classe com.mongodb.DBObject
   */
  def toOBObject(objectId: org.bson.types.ObjectId): DBObject = {
    MongoDBObject("_id" -> objectId)
  }

  def fromMongoObject(dbObject: DBObject, documentClass: Class[_ <: Document]): Document = dbObject match {
    case dbObjectMatch: Any =>
      def processAnnotationsAndGetProperty(field: Field): AnyRef = {
        field.getAnnotation(classOf[Reference]) match {
          case refAnnotation: Reference =>
            if (refAnnotation.association.equals(AssociationType.ONE_TO_ONE)) {
              null.asInstanceOf[AnyRef]
            } else {
              null.asInstanceOf[AnyRef]
            }
          case _ =>
            dbObjectMatch.get(field.getName)
        }
      }
      val document = documentClass.newInstance
      ReflectUtil.loadFieldsRecursively(documentClass).foreach {
        field =>
          field.setAccessible(true)
          if (field.isAnnotationPresent(classOf[DocElement])) {
            field.set(document, processAnnotationsAndGetProperty(field))
          }
      }
      document.setObjectId(dbObject.get("_id").asInstanceOf[ObjectId])
      document
    case _ =>
      null.asInstanceOf[Document]
  }


}