package br.com.gfuture.mongodbhelper

import mongodb.MongoProvider
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBCursor


/**
 * Implementa uma interface de consultas ao mongo mais amigável
 *
 * User: Jeosadache Galvão, josa.galvao@gmail.com
 * Date: 1/25/11
 * Time: 5:39 PM
 */
class Query[T](val entityType: Class[T]) {

  def findById(objectId: org.bson.types.ObjectId): T = {
    val obj = entityType.newInstance
    println(obj)
    val builder = MongoDBObject.newBuilder
    builder += "_id" -> objectId
    val cursor: DBCursor = MongoProvider.getCollection(entityType.getSimpleName).find(builder.result)
    obj
  }

}