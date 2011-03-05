package br.com.gfuture.mongodbhelper

import org.bson.types.ObjectId

/**
 * Implementa uma interface de consultas ao mongo mais amigável
 *
 * User: Jeosadache Galvão, josa.galvao@gmail.com
 * Date: 1/25/11
 * Time: 5:39 PM
 */
class DocumentManager[T <: Entity](val entityType: Class[T]) {

  /**Cria a query apartir de uma query string
   *
   * @param query
   */
  def createQuery: Query[T] = new Query[T](entityType)

  def findById(_id: ObjectId): T = {
    createQuery.addClause("_id", _id).uniqueResult
  }

}