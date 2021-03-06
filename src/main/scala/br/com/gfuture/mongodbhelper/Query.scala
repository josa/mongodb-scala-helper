package br.com.gfuture.mongodbhelper

import mongodb.MongoProvider
import collection.mutable.Builder
import com.mongodb.casbah.commons.{Imports, MongoDBObject}
import org.slf4j.LoggerFactory
import com.mongodb.{DBCursor, DBCollection}

/**Interface de consulta no solr
 *
 * by Jeosadache Galvão, josa.galvao@gmail.com
 */
class Query(val documentClass: Class[_ <: Document]) {

  private lazy val logger = LoggerFactory.getLogger(getClass)

  val queryBuilder = MongoDBObject.newBuilder

  /**Retorna a lista de resultados
   */
  def resultList: List[Document] = {
    val listBuilder: Builder[Document, List[Document]] = List.newBuilder[Document]
    val cursor: DBCursor = collection.find(queryBuilder.result)
    while (cursor.hasNext) listBuilder += DocumentTools.fromMongoObject(cursor.next, documentClass)
    val list: List[Document] = listBuilder.result
    logger.isDebugEnabled() match {
      case true =>
        logger.debug("find %s query[%s], %s itens encontrados".format(documentClass.getSimpleName, cursor.getQuery.toString, cursor.size))
        list.size match {
          case 0 =>
          case _ =>
            logger.debug("documents found:")
            list.foreach({
                entity => logger.debug(entity.toString)
              })
        }
    }

    list
  }

  /**Retorna um resultado para a busca
   */
  def uniqueResult: Document = {
    val dbObject: Imports.DBObject = queryBuilder.result
    val entity = DocumentTools.fromMongoObject(collection.findOne(dbObject), documentClass)
    logger.isDebugEnabled() match {
      case true =>
        logger.debug("find unique query[%s]" format (dbObject.toString))
        entity match {
          case e: Document =>
            logger.debug("document found: %s" format (entity.toString))
          case _ =>
            logger.debug("document not found")
        }
    }
    entity
  }

  /**Adiciona a clausula na query
   *
   * @param o field
   * @param o valor do field
   */
  def addClause(field: String, value: Any): Query = {
    queryBuilder += field -> value
    this
  }
    
  /**Recupera a coleção do documento
   */
  private def collection: DBCollection = MongoProvider.getCollection(documentClass)

}