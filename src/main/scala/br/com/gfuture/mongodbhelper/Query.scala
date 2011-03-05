package br.com.gfuture.mongodbhelper

import com.mongodb.casbah.commons.MongoDBObject
import mongodb.MongoProvider
import com.mongodb.{DBCursor, DBObject, DBCollection}
import collection.mutable.Builder

/**
 * Interface de consulta no solr

 * by Jeosadache Galvão, josa.galvao@gmail.com
 */
class Query[T <: Entity](val documentClass: Class[T]) extends log.Logged {

  val queryBuilder = MongoDBObject.newBuilder

  /**Retorna a lista de resultados
   */
  def resultList: List[T] = {
    val listBuilder: Builder[T, List[T]] = List.newBuilder[T]
    val cursor: DBCursor = collection.find(queryBuilder.result)
    while (cursor.hasNext) listBuilder += Entity.create(cursor.next, documentClass)
    listBuilder.result
  }

  /**Retorna um resultado para a busca
   */
  def uniqueResult: T = {
    val dbObjectResult: DBObject = collection.findOne(queryBuilder.result)
    Entity.create(dbObjectResult, documentClass)
  }

  /**Adiciona a clausula na query
   *
   * @param o field
   * @param o valor do field
   */
  def addClause(field: String, value: Any): Query[T] = {
    queryBuilder += field -> value
    this
  }

  /**Recupera a coleção do documento
   */
  private def collection: DBCollection = MongoProvider.getCollection(documentClass)

}