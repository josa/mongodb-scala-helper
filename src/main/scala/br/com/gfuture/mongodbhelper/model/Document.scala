package br.com.gfuture.mongodbhelper.model

import com.novus.casbah.mongodb.MongoDBObject
import java.lang.reflect.{Field, Method}
import org.bson.types.ObjectId

/**
 * Abstração de um documento mongo
 *
 * User: Jeosadache Galvão
 * Date: Nov 17, 2010
 * Time: 11:56:16 PM
 */
trait Document {

  /**
   * ObjectId do MongoDB
   */
  private var objectId: org.bson.types.ObjectId = null

  def getObjectId(): ObjectId = {this.objectId}

  def setObjectId(objectId: ObjectId) = {this.objectId = objectId}

  def setObjectId(strObjectId: String) = {this.objectId = new ObjectId(strObjectId)}

  /**
   *  Describes the collection name for the entity
   */
  def collectionName: String

  /**
   * Return name of class
   */
  def getEntityName: String = {getClass.getName}

  /**
   * Generates OBObject to search, use only the objectId
   */
  def toUniqueDBObject: com.mongodb.DBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "_id" -> objectId
    return builder.result
  }

  /**
   * Generate DBOjbect by object fields
   */
  def toFullDBObject: com.mongodb.DBObject = {
    val arrayOfMethods: Array[Method] = getClass.getMethods
    val builder = MongoDBObject.newBuilder
    getClass.getDeclaredFields.foreach {
      field =>
        arrayOfMethods.contains()
        val method: Method = getMethodByName(field.getName.capitalize, arrayOfMethods)
        method match {
          case null => {}
          case x => builder += field.getName -> x.invoke(this)
        }
    }
    builder.result
  }

  /**
   * Retorna o método acessor do field no array de métodos
   */
  private def getMethodByName(name: String, arrayOfMethods: Array[Method]): Method = {
    arrayOfMethods.foreach {
      method =>
        if (method.getName.startsWith("get") && method.getName.endsWith(name))
          return method
    }
    return null
  }

}