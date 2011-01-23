package br.com.gfuture.mongodbhelper.model

import dao.converter.ObjectConverter
import com.novus.casbah.mongodb.MongoDBObject
import java.lang.reflect.Field
import mongodb.MongoProvider
import com.mongodb.{DBCollection, DBObject}
import org.bson.types.ObjectId

abstract class Entity[T](val entityType: Class[T]) {

  private var objectId: org.bson.types.ObjectId = null
  protected val transientFields = scala.collection.mutable.Set.empty[String]

  def getObjectId: ObjectId = this.objectId

  def getCollectionName = getClass.getSimpleName

  def getConverter(): ObjectConverter[T] = {
    new ObjectConverter[T](entityType)
  }

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
        if (validateField(field)) {
          field.setAccessible(true)
          builder += field.getName -> field.get(this)
        }
    }
    builder.result
  }

  def validateField(field: Field): Boolean = {
    //Fields transients
    !transientFields.contains(field.getName)
  }

  def save = {
    val dbObject = toDBObject
    getCollection.save(dbObject)
    objectId = dbObject.get("_id").asInstanceOf[ObjectId]
  }

  private def getCollection: DBCollection = {
    MongoProvider.getCollection(getCollectionName)
  }

}