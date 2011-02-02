package br.com.gfuture.mongodbhelper

import mongodb.MongoProvider
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.{DBObject}

/**
 * Implementa uma interface de consultas ao mongo mais amigável
 *
 * User: Jeosadache Galvão, josa.galvao@gmail.com
 * Date: 1/25/11
 * Time: 5:39 PM
 */
class Query[T <: Entity](val entityType: Class[T]) {
  //Entity.create(dbObject, classOf[EntityTest])
  def findById(objectId: org.bson.types.ObjectId): T = {
    val obj = entityType.newInstance
    val builder = MongoDBObject.newBuilder
    builder += "_id" -> objectId
    val dbObject: DBObject = MongoProvider.getCollection(entityType.getSimpleName).findOne(builder.result)
    Entity.create(dbObject, entityType)
  }

}