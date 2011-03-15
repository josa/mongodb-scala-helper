package br.com.gfuture.mongodbhelper

import annotations._
import mongodb.MongoProvider
import org.bson.types.ObjectId
import br.com.gfuture.mongodbhelper.reflect.ReflectUtil
import org.slf4j.LoggerFactory
import com.mongodb.{WriteResult, DBCollection, DBObject}

abstract class Document(val documentClass: Class[_ <: Document]) {

  private lazy val logger = LoggerFactory.getLogger(getClass)

  private var objectId: ObjectId = null

  def save {
    save(() => {
      val mongoObject: DBObject = DocumentTools.toDBObject(this)
      getObjectId match {
        case objectId: ObjectId =>
          val uniqueMongoObject = DocumentTools.toOBObject(this.getObjectId)
          update(uniqueMongoObject, mongoObject, getClass)
        case _ =>
          save(mongoObject, getClass)
          setObjectId(mongoObject.get("_id").asInstanceOf[ObjectId])
      }
    })
  }

  private def save(workSave: () => Unit) {
    try {
      if (logger.isDebugEnabled)
        logger.debug("saving " + this)

      ReflectUtil.callAnnotatedMethod(this, classOf[PrePersist])

      workSave()

      ReflectUtil.callAnnotatedMethod(this, classOf[PosPersist])

      if (logger.isDebugEnabled)
        logger.debug("sucess")
    } catch {
      case e: Exception =>
        logger.error("erro ao salvar " + getClass.getSimpleName, e)
        throw e
    }
  }

  /**
   * Exclui a entidade do mongodb
   *
   * @param a entidade
   * @return voids
   */
  def delete {
    val writeResult: WriteResult = MongoProvider.getCollection(documentClass).remove(DocumentTools.toOBObject(getObjectId))
    if (writeResult.getError != null)
      throw new PersistenceException(writeResult.getError)
  }

  /**Salva o dbObject no mongodb
   */
  private def save(dbObject: DBObject, documentClass: Class[_]) {
    val bCollection: DBCollection = MongoProvider.getCollection(documentClass)
    val writeResult: WriteResult = bCollection.save(dbObject)
    if (writeResult.getError != null)
      throw new PersistenceException(writeResult.getError)
  }

  /**Atualiza o dbObject no mongodb
   *
   * @param o unique dbObject
   * @param o dbObject a ser salvo
   */
  private def update(uniqueDbObject: DBObject, dbObject: DBObject, documentClass: Class[_]) {
    val collection: DBCollection = MongoProvider.getCollection(documentClass)
    val writeResult: WriteResult = collection.update(uniqueDbObject, dbObject)
    if (writeResult.getError != null)
      throw new PersistenceException(writeResult.getError)
  }

  override def equals(that: Any) = that match {
    case other: Document => other.getClass == getClass && other.getObjectId.equals(getObjectId)
    case _ => false
  }

  override def toString = {
    getClass.getSimpleName + ", " + DocumentTools.toDBObject(this).toString
  }

  def getObjectId = this.objectId

  def setObjectId(objectId: ObjectId) {
    this.objectId = objectId
  }

}