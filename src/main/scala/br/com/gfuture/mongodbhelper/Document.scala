package br.com.gfuture.mongodbhelper

import java.lang.reflect.Field
import com.mongodb.casbah.commons.MongoDBObject
import mongodb.MongoProvider
import org.bson.types.ObjectId
import java.lang.String
import com.mongodb.{WriteResult, DBCollection, DBObject}

trait Document extends log.Logged {

  protected var _id: ObjectId = null

  def save = {

    if (logger.isDebugEnabled)
      logger.debug("saving " + getClass.getSimpleName)

    try {

      prePersist

      Document.save(this)

      posPersist

      if (logger.isDebugEnabled)
        logger.debug("sucess")

    } catch {
      case e: Exception =>
        logger.error("erro ao salvar " + getClass.getSimpleName, e)
        throw e
    }
  }

  def delete = Document.delete(this)

  def getTransientFields: Set[String]

  def prePersist = {}

  def posPersist = {}

  def getObjectId: ObjectId = this._id

  def setObjectId(_id: ObjectId) {
    this._id = _id
  }

  def setObjectId(_id: String) {
    this._id = new ObjectId(_id)
  }

  override def toString = {
    getClass.getSimpleName + ", " + Document.toMongoObject(this).toString
  }

}

object Document {

  /**
   * Cria uma instancia de uma entidade
   *
   * @param dbObject, o "json" do mongodb
   * @param documentClass, a tipo que será criado
   */
  def create[T <: Document](dbObject: DBObject, documentClass: Class[T]): T = dbObject match {
    case dbObjectMatch: Any =>
      val document: T = documentClass.newInstance
      loadFieldsRecursively(documentClass).foreach {
        field =>
          Document.validatePersistenteField(document, field.getName) match {
            case true =>
              field.setAccessible(true)
              field.getType.getName match {
                case "br.com.gfuture.mongodbhelper.Reference" =>

                case _ =>
                  field.set(document, dbObjectMatch.get(field.getName))
              }
            case false =>
          }
      }
      document
    case _ =>
      null.asInstanceOf[T]
  }

  /**Salva a entidade no mongodb
   *
   * @param a entidade a ser salva
   */
  def save[T <: Document](document: T) {
    val collectionName: String = MongoProvider.generateCollectionName(document.getClass)
    val mongoObject: DBObject = toMongoObject(document)

    if (document.logger.isDebugEnabled)
      document.logger.debug(mongoObject.toString)

    document._id match {
      case objectId: ObjectId =>
        update(toMongoObject(document.getObjectId), mongoObject, collectionName)
      case _ =>
        document._id = save(mongoObject, collectionName)
    }
  }

  /**Salva o dbObject no mongodb
   *
   * @param o dbObject
   */
  def save[T <: Document](dbObject: DBObject, collectionName: String): ObjectId = {
    val bCollection: DBCollection = MongoProvider.getCollection(collectionName)
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
  def update[T <: Document](uniqueDbObject: DBObject, dbObject: DBObject, collectionName: String) {
    val collection: DBCollection = MongoProvider.getCollection(collectionName)
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
    delete(document._id, document.getClass.getSimpleName.toLowerCase)
  }

  /**
   * Exclui a entidade pelo objectId
   *
   * @param o objectId, identificador único do documento
   * @return void
   */
  def delete(objectId: org.bson.types.ObjectId, collectionName: String) {
    val writeResult: WriteResult = MongoProvider.getCollection(collectionName).remove(toMongoObject(objectId))
    if (writeResult.getError != null)
      throw new PersistenceException(writeResult.getError)
  }

  /**
   * Converte o _id em um objeto de persistencia do mongo
   *
   * @param _id, instancia da classe org.bson.types.ObjectId
   * @return uma instancia da classe com.mongodb.DBObject
   */
  def toMongoObject[T <: Document](objectId: org.bson.types.ObjectId): DBObject = {
    MongoDBObject("_id" -> objectId)
  }

  /**Converte o objeto em um objeto de persistencia do mongo
   *
   * @param document, a entidade a ser convertida
   * @return uma instancia da classe com.mongodb.DBObject
   */
  def toMongoObject[T <: Document](document: T): DBObject = {
    val builder = MongoDBObject.newBuilder
    document.getObjectId match {
      case obj: ObjectId =>
        builder += "_id" -> document.getObjectId
      case _ =>
    }
    loadFieldsRecursively(document.getClass).foreach {
      field =>
        Document.validatePersistenteField(document, field.getName) match {
          case true =>
            field.setAccessible(true)
            field.get(document) match {
              case e: Association =>
                e.getDocument.getObjectId match {
                  case objectId: ObjectId =>
                  case _ =>
                    e.getDocument.save
                }
                builder += field.getName -> e.getDocument.getObjectId
              case null =>
              case _ =>
                builder += field.getName -> field.get(document)
            }
          case false =>
        }
    }
    builder.result
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

  /**
   * Valida os fields persistentes
   *
   * @param document, a entidade em questão
   * @param o field a ser validado
   * @return true caso o field atenda os critérios para serem persistidos
   */
  def validatePersistenteField[T <: Document](entity: T, fieldName: String): Boolean = {
    !entity.getTransientFields.contains(fieldName) && {
      fieldName match {
        case "transientFields" =>
          false
        case "br$com$gfuture$mongodbhelper$Document$$transientFields" =>
          false
        case "bitmap$0" =>
          false
        case "logger" =>
          false
        case _ =>
          true
      }
    }
  }

}