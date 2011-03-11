package br.com.gfuture.mongodbhelper.mongodb

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import br.com.gfuture.mongodbhelper.util.DocumentTest

class MongoProviderSpec extends Spec with ShouldMatchers {

  describe("MongoProvider") {

    it("deveria gerar o nome para a colection") {
      val entity = new DocumentTest()
      val collectionName = MongoProvider.generateCollectionName(entity.getClass)
      collectionName should equal("documenttest")
    }

    it("deveria retornar uma coleção chamada users") {
      val usersColl = MongoProvider.getCollection("users")
      usersColl.getName should equal("users")
    }

    it("deveria retornar um coleção para a entidade DocumentTest") {
      val coll = MongoProvider.getCollection(new DocumentTest().getClass)
      coll.getName should equal("documenttest")
    }

  }

}