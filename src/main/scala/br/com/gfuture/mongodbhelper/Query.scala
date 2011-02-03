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

  /**
   * Busca o objeto pelo seu _id, refere-se ao _id do mongodb
   *
   * @param _id, o _id do mongo
   * @return uma implementação do trait Entity
   */
  def findById(_id: org.bson.types.ObjectId): T = {
    val query = MongoDBObject("_id" -> _id)
    val dbObjectResult: DBObject = Entity.mongoCollection(entityType).findOne(query)
    Entity.create(dbObjectResult, entityType)
  }

}