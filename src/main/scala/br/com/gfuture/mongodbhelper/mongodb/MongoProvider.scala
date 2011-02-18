package br.com.gfuture.mongodbhelper.mongodb

import com.mongodb.DBCollection
import com.mongodb.casbah.MongoConnection

/**
 *  Prover acesso ao mongodb
 *
 *  User: Jeosadache Galvão
 *  Date: Nov 14, 2010
 *  Time: 1:25:31 PM
 *
 */
object MongoProvider {

  //TODO remover isso para um arquivo de property
  val connection = MongoConnection("localhost", 27017)

  /**Gera um nome para a coleção do mongo
   *
   * @param a tipo esperado
   */
  def generateCollectionName[T](classType: Class[T]) = classType.getSimpleName.toLowerCase

  /**Carrega a coleção do mongo para operaçoes diversas
   *
   * @param a entidade
   */
  def getCollection[T](classType: Class[T]): DBCollection =
    connection.getDB("openclesia").getCollection(generateCollectionName(classType))

  /**Carrega a coleção do mongo para operaçoes diversas
   *
   * @param o nome da coleção
   */
  def getCollection(name: String): DBCollection = connection.getDB("openclesia").getCollection(name)

}