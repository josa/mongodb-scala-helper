package br.com.gfuture.mongodbhelper

import annotations._
import java.lang.reflect.Field
import com.mongodb.casbah.commons.MongoDBObject
import mongodb.MongoProvider
import org.bson.types.ObjectId
import java.lang.String
import org.slf4j.LoggerFactory
import com.mongodb.{WriteResult, DBCollection, DBObject}

class Document[T](val typeDocument: Class[T]) {

  protected lazy val logger = LoggerFactory.getLogger(getClass)

  protected var _id: ObjectId = null

  def getObjectId: ObjectId = this._id

  def setObjectId(_id: ObjectId) {
    this._id = _id
  }

  def setObjectId(_id: String) {
    this._id = new ObjectId(_id)
  }

  def getTypeDocument = typeDocument

  def save {

    if (logger.isDebugEnabled)
      logger.debug("saving " + getClass.getSimpleName)

    try {

      MongoDocumentHelper.callPrePersist(asInstanceOf[Document[T]])

      val mongoObject: DBObject = MongoDocumentHelper.toDBObject(this)

      if (logger.isDebugEnabled)
        logger.debug(mongoObject.toString)

      getObjectId match {
        case objectId: ObjectId =>
          update(MongoDocumentHelper.toOBObject(this.getObjectId), mongoObject, this.getClass.asInstanceOf[Class[T]])
        case _ =>
          save(mongoObject, typeDocument)
          setObjectId(mongoObject.get("_id").asInstanceOf[org.bson.types.ObjectId])
      }

      MongoDocumentHelper.callPosPersist(this)

      if (logger.isDebugEnabled)
        logger.debug("sucess")

    } catch {
      case e: Exception =>
        logger.error("erro ao salvar " + getClass.getSimpleName, e)
        throw e
    }
  }


  /**Salva o dbObject no mongodb
   *
   * @param o dbObject
   */
  def save(dbObject: DBObject, documentClass: Class[T]) {
    val bCollection: DBCollection = MongoProvider.getCollection(documentClass)
    val writeResult: WriteResult = bCollection.save(dbObject)

    if (writeResult.getError != null)
      throw new PersistenceException(writeResult.getError)

  }



  override def toString = {
    getClass.getSimpleName + ", " + MongoDocumentHelper.toDBObject(this).toString
  }


  /**Atualiza o dbObject no mongodb
   *
   * @param o unique dbObject
   * @param o dbObject a ser salvo
   */
  private def update(uniqueDbObject: DBObject, dbObject: DBObject, documentClass: Class[T]) {
    val collection: DBCollection = MongoProvider.getCollection(documentClass)
    val writeResult: WriteResult = collection.update(uniqueDbObject, dbObject)
    if (writeResult.getError != null)
      throw new PersistenceException(writeResult.getError)
  }

  /**
   * Exclui a entidade do mongodb
   *
   * @param a entidade
   * @return voids
   */
  def delete {
    val writeResult: WriteResult = MongoProvider.getCollection(typeDocument).remove(MongoDocumentHelper.toOBObject(getObjectId))
    if (writeResult.getError != null)
      throw new PersistenceException(writeResult.getError)
  }

}

object MongoDocumentHelper {

  /**Converte o objeto em um objeto de persistencia do mongo
   *
   * @param document, a entidade a ser convertida
   * @return uma instancia da classe com.mongodb.DBObject
   */
  def toDBObject[T](document: Document[T]): DBObject = {

    val builder = MongoDBObject.newBuilder

    def processAnnotationAndGenerateElement(field: Field): (String, AnyRef) = {
      field.getAnnotation(classOf[Reference]) match {
        case refAnnotation: Reference =>
          val documentReference = field.get(document).asInstanceOf[Document[T]]
          if (!refAnnotation.cascade.equals(CascadeType.SAVE) && documentReference.getObjectId == null)
            throw new MappingException(field.getName + ": objectId not exists")

          if (refAnnotation.cascade.equals(CascadeType.SAVE))
            documentReference.save

          field.getName -> documentReference.getObjectId
        case null =>
          field.getName -> field.get(document)
      }
    }

    loadFieldsRecursively(document.getClass).foreach {
      field =>
        field.setAccessible(true)
        if (field.isAnnotationPresent(classOf[DocElement]) && field.get(document) != null)
          builder += processAnnotationAndGenerateElement(field)
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
  def toOBObject[T](objectId: org.bson.types.ObjectId): DBObject = {
    MongoDBObject("_id" -> objectId)
  }

  /**
   * Cria uma instancia de uma entidade
   *
   * @param dbObject, o "json" do mongodb
   * @param documentClass, a tipo que será criado   [T](document: Document[T])
   */
  def fromMongoObject[T](dbObject: DBObject, documentClass: Class[T]): T = dbObject match {
    case dbObjectMatch: Any =>

      def processAnnotationsAndGetProperty(field: Field): AnyRef = {
        field.getAnnotation(classOf[Reference]) match {
          case refAnnotation: Reference =>
            if (refAnnotation.association.equals(AssociationType.ONE_TO_ONE)) {
              val clazz = field.getType
              println(clazz)
              //val documentManager = new DocumentManager[T](document.getTypeDocument)
              //documentManager.findById(dbObjectMatch.get(field.getName).asInstanceOf[ObjectId])
              null.asInstanceOf[AnyRef]
            } else {
              null.asInstanceOf[AnyRef]
            }
          case _ =>
            dbObjectMatch.get(field.getName)
        }
      }

      val document = documentClass.newInstance.asInstanceOf[T]
      loadFieldsRecursively(documentClass).foreach {
        field =>
          field.setAccessible(true)
          if (field.isAnnotationPresent(classOf[DocElement])) {
            field.set(document, processAnnotationsAndGetProperty(field))
          }
      }
      documentClass.getField("_id").set(document, dbObject.get("_id").asInstanceOf[ObjectId])
      document
    case _ =>
      null.asInstanceOf[T]
  }

  /**Pesquisa um field da classe e superclasses reculsirvamente
   *
   * @param o nome do field
   * @param a classe da entidade
   *
   */
  def findField[T](name: String, documentClass: Class[T]): Field = {
    try {
      documentClass.getDeclaredField(name)
    } catch {
      case e: java.lang.NoSuchFieldException =>
        documentClass.getSuperclass match {
          case x: Class[T] =>
            findField(name, documentClass.getSuperclass)
          case _ =>
            throw new RuntimeException("field not found: " + documentClass.getName + "[" + name + "]")
        }

    }
  }

  /**Carrega os fields da classe e superclasses recusivamente
   *
   * @param a classe
   *
   */
  def loadFieldsRecursively[T](documentClass: Class[T]): List[Field] = {
    loadFieldsRecursively(documentClass, List.empty[Field])
  }

  /**Carrega os fields da classe e superclasses recusivamente
   *
   * @param a classe
   * @param a lista de fields
   *
   */
  def loadFieldsRecursively[T](documentClass: Class[T], fieldList: List[Field]): List[Field] = {
    documentClass match {
      case c: Class[T] =>
        loadFieldsRecursively(documentClass.getSuperclass, fieldList union c.getDeclaredFields.toList)
      case _ =>
        fieldList
    }
  }

  /**Chama o método anotado como @PrePersist
   */
  def callPrePersist[T](document:Document[T]) {
    callAnnotatedMethod(document, classOf[PrePersist])
  }

  /**Chama o método anotado como @PosPersist
   */
  def callPosPersist[T](document:Document[T]) {
    callAnnotatedMethod(document, classOf[PosPersist])
  }
    /**Chama um método anotado pela anotação passada como parametro
   */
  def callAnnotatedMethod[T](document:Document[T], annotation: Class[_ <: java.lang.annotation.Annotation]) {
    getClass.getMethods.foreach({
      m =>
        if (m.isAnnotationPresent(annotation)) {
          m.invoke(document)
        }
    })
  }

}