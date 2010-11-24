package br.com.gfuture.mongodbhelper.model.mongodb

import com.novus.casbah.mongodb.{MongoCollection, MongoConnection}
import com.mongodb.DBCollection

/**
 *  Prover acesso ao mongodb
 *
 *  User: Jeosadache Galvão
 *  Date: Nov 14, 2010
 *  Time: 1:25:31 PM
 *
 */
object MongoProvider {

  val connection = MongoConnection("localhost", 27017)

  def getCollection(name: String): DBCollection = {
    connection.getDB("openclesia").getCollection(name)
  }

}