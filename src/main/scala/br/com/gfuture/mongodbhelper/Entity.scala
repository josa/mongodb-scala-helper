package br.com.gfuture.mongodbhelper

import java.lang.reflect.Field
import com.mongodb.{DBCollection, DBObject}

import com.mongodb.casbah.commons.MongoDBObject
import mongodb.MongoProvider
import org.bson.types.ObjectId

trait Entity {

  var _id: org.bson.types.ObjectId = null

  private val transientFields = scala.collection.mutable.Set.empty[String]

  def getObjectId: org.bson.types.ObjectId = this._id

  def save = Entity.save(this)

  def delete = Entity.delete(this)

  def getTransientFields = this.transientFields

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

  def save[T <: Entity](entity: T) {
    entity._id = save(toMongoObject(entity), entity.getClass.getSimpleName.toLowerCase)
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
    builder += "_id" -> entity.getObjectId
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