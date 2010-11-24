package br.com.gfuture.mongodbhelper.model.mongodb

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, Spec}
import br.com.gfuture.mongodbhelper.model.EntityTest

class DocumentSpec extends Spec with ShouldMatchers with BeforeAndAfterEach {
  describe("Document") {
    describe("DBObject") {

      it("should return name of class") {
        val entityTest = new EntityTest("title");
        entityTest.getEntityName should equal("br.com.gfuture.mongodbhelper.model.EntityTest")
      }

      it("should generate") {
        val entityTest = new EntityTest("My Title");
        val dbObject = entityTest.toFullDBObject
        dbObject.get("title") should equal("My Title")
        dbObject.keySet.size should equal(1)
      }

    }
  }
}