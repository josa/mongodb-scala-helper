package br.com.gfuture.mongodbhelper

import mongodb.MongoProvider
import com.mongodb.{DBCursor, DBCollection}
import collection.mutable.Builder
import com.mongodb.casbah.commons.{Imports, MongoDBObject}

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
    if (logger.isDebugEnabled())
      logger.debug("find %s query[%s], %s itens encontrados".format(documentClass.getSimpleName, cursor.getQuery.toString, cursor.size))
    while (cursor.hasNext) listBuilder += Entity.create(cursor.next, documentClass)
    listBuilder.result
  }

  /**Retorna um resultado para a busca
   */
  def uniqueResult: T = {
    val dbObject: Imports.DBObject = queryBuilder.result
    if (logger.isDebugEnabled())
      logger.debug("find unique query[%s]" format (dbObject.toString))
    Entity.create(collection.findOne(dbObject), documentClass)
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