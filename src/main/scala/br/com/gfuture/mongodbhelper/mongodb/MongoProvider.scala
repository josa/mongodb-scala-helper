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

  val connection = MongoConnection("localhost", 27017)

  def getCollection(name: String): DBCollection = {
    connection.getDB("openclesia").getCollection(name)
  }

}