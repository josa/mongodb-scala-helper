package br.com.gfuture.mongodbhelper

import java.lang.reflect.Field
import com.mongodb.{DBCollection, DBObject}

import com.mongodb.casbah.commons.MongoDBObject
import mongodb.MongoProvider

trait Entity {

  protected var _id: org.bson.types.ObjectId = null

  protected val transientFields = scala.collection.mutable.Set.empty[String]

  def getObjectId: org.bson.types.ObjectId = this._id

  def toUniqueMongoObject = Entity.toMongoObject(_id)

  def toMongoObject = Entity.toMongoObject(this)

  def save = Entity.save(this)

  def getTransientFields = this.transientFields

  override def equals(that: Any) = that match {
    case _ => throw new RuntimeException(getClass.getName + ", é obrigatório implementar o método equals")
  }

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
      val arrayOfFields: Array[Field] = entityClass.getDeclaredFields
      entityClass.getDeclaredFields.foreach {
        field =>
          Entity.validatePersistenteField(entity, field) match {
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

  /**
   * Converte o _id em um objeto de persistencia do mongo
   *
   * @param _id, instancia da classe org.bson.types.ObjectId
   * @return uma instancia da classe com.mongodb.DBObject
   */
  def toMongoObject[T <: Entity](objectId: org.bson.types.ObjectId): DBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "_id" -> objectId
    return builder.result
  }

  /**
   * Converte o objeto em um objeto de persistencia do mongo
   *
   * @param entity, a entidade a ser convertida
   * @return uma instancia da classe com.mongodb.DBObject
   */
  def toMongoObject[T <: Entity](entity: T): DBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "_id" -> entity.getObjectId
    entity.getClass.getDeclaredFields.foreach {
      field =>
        Entity.validatePersistenteField(entity, field) match {
          case true =>
            field.setAccessible(true)
            builder += field.getName -> field.get(entity)
          case false =>
        }
    }
    builder.result
  }

  /**
   * Salva a entidade no mongodb
   *
   * @param entity, a entidade a ser salva
   *
   */
  def save[T <: Entity](entity: T) = {
    val dbObject = toMongoObject(entity)
    calculateCollection(entity).save(dbObject)
    val objectIdField: Field = entity.getClass.getDeclaredField("_id")
    objectIdField.setAccessible(true)
    objectIdField.set(entity, dbObject.get("_id").asInstanceOf[org.bson.types.ObjectId])
  }

  def validatePersistenteField[T <: Entity](entity: T, field: Field): Boolean = {
    !entity.getTransientFields.contains(field.getName) && !field.getName.equals("transientFields")
  }

  /**
   * Calcula a coleção em que a entidade vai ser salva
   *
   * @param entity, a entidade base
   */
  private def calculateCollection[T <: Entity](entity: T): DBCollection = {
    MongoProvider.getCollection(entity.getClass.getSimpleName)
  }

}