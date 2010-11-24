package br.com.gfuture.mongodbhelper.model.mongodb

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec

class MongoProviderSpec extends Spec with ShouldMatchers {

  describe("MongoProvider"){

    it("should return collection: users"){
      val usersColl = MongoProvider.getCollection("users")
      usersColl.getName should equal("users")
    }

  }

}