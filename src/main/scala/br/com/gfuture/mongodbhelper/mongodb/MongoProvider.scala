package br.com.gfuture.mongodbhelper.mongodb

import com.mongodb.DBCollection
import br.com.gfuture.mongodbhelper.configuration.Configuration
import com.mongodb.casbah.{MongoDB, MongoConnection}
import org.slf4j.LoggerFactory

/**
 *  Prover acesso ao mongodb
 *
 *  User: Jeosadache Galvão
 *  Date: Nov 14, 2010
 *  Time: 1:25:31 PM
 *
 */
object MongoProvider extends MongoProvider

trait MongoProvider {

  protected lazy val logger = LoggerFactory.getLogger(getClass)

  lazy val connection = {
    logger.info("connecting to the server MongoDB: %s:%s" format (Configuration.mongodbHost, Configuration.mongodbPort))
    MongoConnection(Configuration.mongodbHost, Configuration.mongodbPort)
  }

  /**Carrega a coleção do mongo para operaçoes diversas
   *
   * @param a entidade
   */
  def getCollection[T](classType: Class[T]): DBCollection =
    getCollection(generateCollectionName(classType))

  /**Carrega a coleção do mongo para operaçoes diversas
   *
   * @param o nome da coleção
   */
  def getCollection(name: String): DBCollection =
    connection.getDB(Configuration.mongodbDatabase).getCollection(name)

  /**Gera um nome para a coleção do mongo
   *
   * @param a tipo esperado
   */
  private def generateCollectionName[T](classType: Class[T]) =
    classType.getSimpleName.toLowerCase

}