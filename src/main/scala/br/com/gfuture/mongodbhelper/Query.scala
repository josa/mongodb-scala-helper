package br.com.gfuture.mongodbhelper


/**
 * Implementa uma interface de consultas ao mongo mais amigável
 *
 * User: Jeosadache Galvão, josa.galvao@gmail.com
 * Date: 1/25/11
 * Time: 5:39 PM
 */
class Query[T](val entityType: Class[T]){

  def findById(id:org.bson.types.ObjectId):T = entityType.newInstance

}