package br.com.gfuture.mongodbhelper

import org.bson.types.ObjectId

/**
 * Implementa uma interface de consultas ao mongo mais amigável
 *
 * User: Jeosadache Galvão, josa.galvao@gmail.com
 * Date: 1/25/11
 * Time: 5:39 PM
 */
class DocumentManager(val entityType: Class[_<:Document]) {

  /**Cria a query apartir de uma query string
   *
   * @param query
   */
  def createQuery: Query = new Query(entityType)

  /**Busca o documento pela string que representa o id
   *
   * @param a string que representa o objectId
   */
  def findById(_id: String): Document = {
    findById(new ObjectId(_id))
  }

  /**Busca o documento pelo id
   *
   * @param o objectId
   */
  def findById(_id: ObjectId): Document = {
    createQuery.addClause("_id", _id).uniqueResult
  }

}