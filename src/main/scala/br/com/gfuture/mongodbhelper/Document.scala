package br.com.gfuture.mongodbhelper

import annotations._
import java.lang.reflect.Field
import com.mongodb.casbah.commons.MongoDBObject
import mongodb.MongoProvider
import org.bson.types.ObjectId
import java.lang.String
import org.slf4j.LoggerFactory
import com.mongodb.{WriteResult, DBCollection, DBObject}

trait Document {

  protected lazy val logger = LoggerFactory.getLogger(getClass)

  protected var _id: ObjectId = null

  def save = {

    if (logger.isDebugEnabled)
      logger.debug("saving " + getClass.getSimpleName)

    try {

      Document.callPrePersist(this)

      Document.save(this)

      Document.callPosPersist(this)

      if (logger.isDebugEnabled)
        logger.debug("sucess")

    } catch {
      case e: Exception =>
        logger.error("erro ao salvar " + getClass.getSimpleName, e)
        throw e
    }
  }

  def delete = Document.delete(this)

  override def toString = {
    getClass.getSimpleName + ", " + Document.toDBObject(this).toString
  }

  def getObjectId: ObjectId = this._id

  def setObjectId(_id: ObjectId) {
    this._id = _id
  }

  def setObjectId(_id: String) {
    this._id = new ObjectId(_id)
  }

}

object Document {

  /**Converte o objeto em um objeto de persistencia do mongo
   *
   * @param document, a entidade a ser convertida
   * @return uma instancia da classe com.mongodb.DBObject
   */
  def toDBObject[T <: Document](document: T): DBObject = {

    val builder = MongoDBObject.newBuilder

    def processAnnotationAndGenerateElement(field: Field): (String, AnyRef) = {
      field.getAnnotation(classOf[Reference]) match {
        case refAnnotation: Reference =>
          val documentReference = field.get(document).asInstanceOf[Document]
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
  def toOBObject[T <: Document](objectId: org.bson.types.ObjectId): DBObject = {
    MongoDBObject("_id" -> objectId)
  }

  /**
   * Cria uma instancia de uma entidade
   *
   * @param dbObject, o "json" do mongodb
   * @param documentClass, a tipo que será criado
   */
  def fromMongoObject[T <: Document](dbObject: DBObject, documentClass: Class[T]): T = dbObject match {
    case dbObjectMatch: Any =>
      val document: T = documentClass.newInstance
      loadFieldsRecursively(documentClass).foreach {
        field =>
          field.isAnnotationPresent(classOf[DocElement]) match {
            case true =>
              field.setAccessible(true)
              field.set(document, dbObjectMatch.get(field.getName))
            case false =>
          }
      }
      document.setObjectId(dbObject.get("_id").asInstanceOf[ObjectId])
      document
    case _ =>
      null.asInstanceOf[T]
  }

  /**Salva a entidade no mongodb
   *
   * @param a entidade a ser salva
   */
  def save[T <: Document](document: T) {
    val mongoObject: DBObject = toDBObject(document)

    if (document.logger.isDebugEnabled)
      document.logger.debug(mongoObject.toString)

    document._id match {
      case objectId: ObjectId =>
        update(toOBObject(document.getObjectId), mongoObject, document.getClass.asInstanceOf[Class[T]])
      case _ =>
        document._id = save(mongoObject, document.getClass.asInstanceOf[Class[T]])
    }
  }

  /**Salva o dbObject no mongodb
   *
   * @param o dbObject
   */
  def save[T <: Document](dbObject: DBObject, documentClass: Class[T]): ObjectId = {
    val bCollection: DBCollection = MongoProvider.getCollection(documentClass)
    val writeResult: WriteResult = bCollection.save(dbObject)

    if (writeResult.getError != null)
      throw new PersistenceException(writeResult.getError)

    dbObject.get("_id").asInstanceOf[org.bson.types.ObjectId]
  }

  /**Atualiza o dbObject no mongodb
   *
   * @param o unique dbObject
   * @param o dbObject a ser salvo
   */
  private def update[T <: Document](uniqueDbObject: DBObject, dbObject: DBObject, documentClass: Class[T]) {
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
  def delete[T <: Document](document: T) {
    delete(document._id, document.getClass.asInstanceOf[Class[T]])
  }

  /**
   * Exclui a entidade pelo objectId
   *
   * @param o objectId, identificador único do documento
   * @return void
   */
  def delete[T <: Document](objectId: org.bson.types.ObjectId, documentClass: Class[T]) {
    val writeResult: WriteResult = MongoProvider.getCollection(documentClass).remove(toOBObject(objectId))
    if (writeResult.getError != null)
      throw new PersistenceException(writeResult.getError)
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
  def callPrePersist[T <: Document](document: T) {
    callAnnotatedMethod(document, classOf[PrePersist])
  }

  /**Chama o método anotado como @PosPersist
   */
  def callPosPersist[T <: Document](document: T) {
    callAnnotatedMethod(document, classOf[PosPersist])
  }

  /**Chama um método anotado pela anotação passada como parametro
   */
  def callAnnotatedMethod[T <: Document](document: T, annotation: Class[_ <: java.lang.annotation.Annotation]) {
    document.getClass.getMethods.foreach({
      m =>
        if (m.isAnnotationPresent(annotation)) {
          m.invoke(document)
        }
    })
  }

}