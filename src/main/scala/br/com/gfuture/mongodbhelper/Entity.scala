package br.com.gfuture.mongodbhelper

import java.lang.reflect.Field
import com.mongodb.{DBCollection, DBObject}

import com.mongodb.casbah.commons.MongoDBObject
import mongodb.MongoProvider

trait Entity{

  private var objectId: org.bson.types.ObjectId = null
  protected val transientFields = scala.collection.mutable.Set.empty[String]

  def getObjectId: org.bson.types.ObjectId = this.objectId

  def toDBObjectId: com.mongodb.DBObject = {
    if (getObjectId == null)
      return null
    val builder = MongoDBObject.newBuilder
    builder += "_id" -> objectId
    return builder.result
  }

  def toDBObject(): DBObject = {
    val builder = MongoDBObject.newBuilder
    if (getObjectId != null)
      builder += "_id" -> getObjectId
    getClass.getDeclaredFields.foreach {
      field =>
        if (isTransientField(field)) {
          field.setAccessible(true)
          builder += field.getName -> field.get(this)
        }
    }
    builder.result
  }

  /**
   * Salva o objeto no mongodb
   */
  def save = {
    val dbObject = toDBObject
    getCollection.save(dbObject)
    objectId = dbObject.get("_id").asInstanceOf[org.bson.types.ObjectId]
  }

  override def equals(that: Any) = that match {
    case _ => throw new RuntimeException(getClass.getName + ", é obrigatório implementar o método equals")
  }

  private def getCollection: DBCollection = MongoProvider.getCollection(getClass.getSimpleName)

  private def isTransientField(field: Field): Boolean = !transientFields.contains(field.getName)

}

object Entity{

  def create(entityClass: Class[Entity]):Entity = {
    entityClass.newInstance
  }

}