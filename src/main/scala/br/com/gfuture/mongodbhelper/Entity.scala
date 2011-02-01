package br.com.gfuture.mongodbhelper

import java.lang.reflect.Field
import com.mongodb.{DBCollection, DBObject}

import com.mongodb.casbah.commons.MongoDBObject
import mongodb.MongoProvider

trait Entity {

  protected var objectId: org.bson.types.ObjectId = null

  protected val transientFields = scala.collection.mutable.Set.empty[String]

  def getObjectId: org.bson.types.ObjectId = this.objectId

  def toUniqueMongoObject = Entity.toMongoObject(objectId)

  def toMongoObject = Entity.toMongoObject(this)

  def save = Entity.save(this)

  private def validate(field: Field): Boolean = !transientFields.contains(field.getName)

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
  def create[T](dbObject: DBObject, entityClass: Class[T]): T = {
    val entity: T = entityClass.newInstance
    entityClass.getDeclaredFields.foreach {
      field =>
        field.setAccessible(true)
        field.set(entity, dbObject.get(field.getName))
    }
    entity
  }

  /**
   * Converte o objectId em um objeto de persistencia do mongo
   *
   * @param objectId, instancia da classe org.bson.types.ObjectId
   * @return uma instancia da classe com.mongodb.DBObject
   */
  def toMongoObject[T<:Entity](objectId: org.bson.types.ObjectId): DBObject = {
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
  def toMongoObject[T<:Entity](entity: T): DBObject = {
    val builder = MongoDBObject.newBuilder

    if (entity.getObjectId != null)
      builder += "_id" -> entity.getObjectId

    entity.getClass.getDeclaredFields.foreach {
      field =>
        if (entity.validate(field)) {
          field.setAccessible(true)
          builder += field.getName -> field.get(entity)
        }
    }
    builder.result
  }

  def save[T<:Entity](entity: T) = {
    val dbObject = toMongoObject(entity)
    calculateCollection(entity).save(dbObject)
    val objectIdField: Field = entity.getClass.getDeclaredField("objectId")
    objectIdField.setAccessible(true)
    objectIdField.set(entity, dbObject.get("_id").asInstanceOf[org.bson.types.ObjectId])
  }

  private def calculateCollection[T<:Entity](entity: T):DBCollection = {
     MongoProvider.getCollection(entity.getClass.getSimpleName)
  }

}