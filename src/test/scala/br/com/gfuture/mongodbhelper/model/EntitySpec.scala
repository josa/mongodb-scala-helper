package br.com.gfuture.mongodbhelper.model

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}

class EntitySpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  var entity:EntityTest = null

  override def beforeEach() {
    entity = new EntityTest
  }

  describe("EntitySpec") {

    describe("DBObject") {

      it("should load title") {
        entity.title = "My Title"
        val dbObject = entity.toDBObject
        dbObject.get("title") should equal("My Title")
      }

      it("should load description") {
        entity.description = "My Description"
        val dbObject = entity.toDBObject
        dbObject.get("description") should equal("My Description")
      }

      it("should not load transient field") {
        entity.transient = "value of transient"
        val dbObject = entity.toDBObject
        dbObject.get("transient") should equal(null)
      }

    }

    describe("Collection") {

      it("should return default collection") {
        val collectionName = entity.getCollectionName
        collectionName should equal("EntityTest");
      }

    }

    describe("persistence") {

      describe("save") {

        it("should save entity") {
          entity.title = "My Title"
          entity.save
          entity.objectId should not equal (null)
        }

      }

    }

  }

}