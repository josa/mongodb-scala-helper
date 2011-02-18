package br.com.gfuture.mongodbhelper.mongodb

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import br.com.gfuture.mongodbhelper.util.EntityTest

class MongoProviderSpec extends Spec with ShouldMatchers {

  describe("MongoProvider") {

    it("deveria gerar o nome para a colection") {
      val entity = new EntityTest()
      val collectionName = MongoProvider.generateCollectionName(entity.getClass)
      collectionName should equal("entitytest")
    }

    it("deveria retornar uma coleção chamada users") {
      val usersColl = MongoProvider.getCollection("users")
      usersColl.getName should equal("users")
    }

    it("deveria retornar um coleção para a entidade EntityTest") {
      val coll = MongoProvider.getCollection(new EntityTest().getClass)
      coll.getName should equal("entitytest")
    }

  }

}