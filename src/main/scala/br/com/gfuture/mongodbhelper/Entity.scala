package br.com.gfuture.mongodbhelper

import java.lang.reflect.Field
import com.mongodb.{DBCollection, DBObject}

import com.mongodb.casbah.commons.MongoDBObject
import mongodb.MongoProvider
import org.bson.types.ObjectId
import java.lang.String

trait Entity {

  var _id: org.bson.types.ObjectId = null

  def getObjectId: org.bson.types.ObjectId = this._id

  def save = {

    prePersist

    Entity.save(this)

    posPersist

  }

  def delete = Entity.delete(this)

  def getTransientFields: Set[String]

  def prePersist = {}

  def posPersist = {}

  override def toString = {getClass.getSimpleName + ", "+ Entity.toMongoObject(this).toString}

}

object Entity {

  /**
   * Cria uma instancia de uma entidade
   *
   * @param dbObject, o "json" do mongodb
   * @param entityClass, a tipo que será criado
   */
  def create[T <: Entity](dbObject: DBObject, entityClass: Class[T]): T = dbObject match {
    case dbObjectMatch: Any =>
      val entity: T = entityClass.newInstance
      loadFieldsRecursively(entityClass).foreach {
        field =>
          Entity.validatePersistenteField(entity, field.getName) match {
            case true =>
              field.setAccessible(true)
              field.set(entity, dbObjectMatch.get(field.getName))
            case false =>
          }
      }
      entity
    case _ =>
      null.asInstanceOf[T]
  }

  /**Salva a entidade no mongodb
   *
   * @param a entidade a ser salva
   */
  def save[T <: Entity](entity: T) {
    val collectionName: String = MongoProvider.generateCollectionName(entity.getClass)
    entity._id match {
      case objectId: ObjectId =>
        update(toMongoObject(entity.getObjectId), toMongoObject(entity), collectionName)
      case _ =>
        entity._id = save(toMongoObject(entity), collectionName)
    }
  }

  /**Salva o dbObject no mongodb
   *
   * @param o dbObject
   */
  def save[T <: Entity](dbObject: DBObject, collectionName: String): ObjectId = {
    val bCollection: DBCollection = MongoProvider.getCollection(collectionName)
    bCollection.save(dbObject)
    dbObject.get("_id").asInstanceOf[org.bson.types.ObjectId]
  }

  /**Atualiza o dbObject no mongodb
   *
   * @param o unique dbObject
   * @param o dbObject a ser salvo
   */
  def update[T <: Entity](uniqueDbObject: DBObject, dbObject: DBObject, collectionName: String) {
    val collection: DBCollection = MongoProvider.getCollection(collectionName)
    println(collection.update(uniqueDbObject, dbObject))
  }

  /**
   * Exclui a entidade do mongodb
   *
   * @param a entidade
   * @return voids
   */
  def delete[T <: Entity](entity: T) {
    delete(entity._id, entity.getClass.getSimpleName.toLowerCase)
  }

  /**
   * Exclui a entidade pelo objectId
   *
   * @param o objectId, identificador único do documento
   * @return void
   */
  def delete(objectId: org.bson.types.ObjectId, collectionName: String) {
    MongoProvider.getCollection(collectionName).remove(toMongoObject(objectId))
  }

  /**
   * Converte o _id em um objeto de persistencia do mongo
   *
   * @param _id, instancia da classe org.bson.types.ObjectId
   * @return uma instancia da classe com.mongodb.DBObject
   */
  def toMongoObject[T <: Entity](objectId: org.bson.types.ObjectId): DBObject = {
    MongoDBObject("_id" -> objectId)
  }

  /**Converte o objeto em um objeto de persistencia do mongo
   *
   * @param entity, a entidade a ser convertida
   * @return uma instancia da classe com.mongodb.DBObject
   */
  def toMongoObject[T <: Entity](entity: T): DBObject = {
    val builder = MongoDBObject.newBuilder
    entity.getObjectId match {
      case obj: ObjectId =>
        builder += "_id" -> entity.getObjectId
      case _ =>
    }
    loadFieldsRecursively(entity.getClass).foreach {
      field =>
        Entity.validatePersistenteField(entity, field.getName) match {
          case true =>
            field.setAccessible(true)
            builder += field.getName -> field.get(entity)
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
  def findField[T](name: String, entityClass: Class[T]): Field = {
    try {
      entityClass.getDeclaredField(name)
    } catch {
      case e: java.lang.NoSuchFieldException =>
        entityClass.getSuperclass match {
          case x: Class[T] =>
            findField(name, entityClass.getSuperclass)
          case _ =>
            throw new RuntimeException("field not found: " + entityClass.getName + "[" + name + "]")
        }

    }
  }

  /**Carrega os fields da classe e superclasses recusivamente
   *
   * @param a classe
   *
   */
  def loadFieldsRecursively[T](entityClass: Class[T]): List[Field] = {
    loadFieldsRecursively(entityClass, List.empty[Field])
  }

  /**Carrega os fields da classe e superclasses recusivamente
   *
   * @param a classe
   * @param a lista de fields
   *
   */
  def loadFieldsRecursively[T](entityClass: Class[T], fieldList: List[Field]): List[Field] = {
    entityClass match {
      case c: Class[T] =>
        loadFieldsRecursively(entityClass.getSuperclass, fieldList union c.getDeclaredFields.toList)
      case _ =>
        fieldList
    }
  }

  /**
   * Valida os fields persistentes
   *
   * @param entity, a entidade em questão
   * @param o field a ser validado
   * @return true caso o field atenda os critérios para serem persistidos
   */
  def validatePersistenteField[T <: Entity](entity: T, fieldName: String): Boolean = {
    !entity.getTransientFields.contains(fieldName) && {
      fieldName match {
        case "transientFields" =>
          false
        case "br$com$gfuture$mongodbhelper$Entity$$transientFields" =>
          false
        case _ =>
          true
      }
    }
  }

}