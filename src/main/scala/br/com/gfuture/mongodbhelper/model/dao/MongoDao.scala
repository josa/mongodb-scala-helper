package br.com.gfuture.mongodbhelper.model.dao

import org.bson.types.ObjectId
import br.com.gfuture.mongodbhelper.model.mongodb.MongoProvider
import br.com.gfuture.mongodbhelper.model.Document
import com.novus.casbah.mongodb.MongoDBObject
import com.mongodb.{DBObject, DBCollection}

/**
 * Data Acess Object - generic implementation
 *
 * User: Jeosadache Galvão
 * Date: Nov 20, 2010
 * Time: 3:04:09 PM
 */
class MongoDao[T] {

  //private var dataType:T = Class.forName("Any").newInstance.asInstanceOf[T]
       /**
   * Salva o objeto no mongodb, o id gerando pelo driver do mongo é atribuido
   * ao _id do document passado como parametro
  def findByObjectId(objectId: ObjectId): Document = {
    //println(this.dataType)
    val dbObject: DBObject = (MongoDBObject.newBuilder += "_id" -> objectId).result
    val className = dbObject.get("className")
    val clazz = Class.forName(className.asInstanceOf[String])
    clazz.newInstance.asInstanceOf[Document]
  }



  def save(document: Document) = {
    val dbObject = document.toFullDBObject
    getCollection(document.collectionName).save(dbObject)
    document.setObjectId(dbObject.get("_id").asInstanceOf[ObjectId])
  }
 */

  /**
   * Deletes the object MongoDB

  def delete(document: Document) = {
    getCollection(document.collectionName).remove(document.toFullDBObject)
  }

  */
  /**
   * Return the mongo collection
   x
  private def getCollection(collectionName: String): DBCollection = {
    MongoProvider.getCollection(collectionName)
  }
  */

}