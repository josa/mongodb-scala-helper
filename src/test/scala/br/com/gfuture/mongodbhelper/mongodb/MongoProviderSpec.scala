package br.com.gfuture.mongodbhelper.mongodb

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import br.com.gfuture.mongodbhelper.Document

class MongoProviderSpec extends Spec with ShouldMatchers {

  class DocumentExample extends Document

  describe("MongoProvider") {

    it("deveria gerar o nome para a colection") {
      val entity = new DocumentExample()
      val collectionName = MongoProvider.getCollection(entity.getClass).getName
      collectionName should equal("documentexample")
    }

    it("deveria retornar uma coleção chamada users") {
      val usersColl = MongoProvider.getCollection("users")
      usersColl.getName should equal("users")
    }

    it("deveria retornar um coleção para a o document DocumentExample") {
      val coll = MongoProvider.getCollection(new DocumentExample().getClass)
      coll.getName should equal("documentexample")
    }

  }

}