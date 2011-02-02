package br.com.gfuture.mongodbhelper

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject

class EntitySpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  var entity: EntityTest = null
  var dbObject: DBObject = null

  override def beforeEach() {
    entity = new EntityTest

    val builder = MongoDBObject.newBuilder
    builder += "_id" -> org.bson.types.ObjectId.get
    builder += "title" -> "Título"
    dbObject = builder.result

  }

  describe("Entity") {

    describe("create") {

      it("should create instanceof EntityTest") {
        val entity: EntityTest = Entity.create(dbObject, classOf[EntityTest])
        entity.title should equal(dbObject.get("title"))
        entity.getObjectId should equal(dbObject.get("_id"))
      }

      it("should not load transient fields") {
        val entity: EntityTest = Entity.create(dbObject, classOf[EntityTest])
        entity.transient should equal(null)
      }

    }

    describe("to DBObject") {

      it("should load title") {
        entity.title = "My Title"
        val dbObject = entity.toMongoObject
        dbObject.get("title") should equal("My Title")
      }

      it("should load description") {
        entity.description = "My Description"
        val dbObject = entity.toMongoObject
        dbObject.get("description") should equal("My Description")
      }

      it("should not load transient field") {
        entity.transient = "value of transient"
        val dbObject = entity.toMongoObject
        dbObject.get("transient") should equal(null)
      }

    }

    describe("persistence") {

      describe("save") {

        it("should save entity") {
          entity.title = "My Title"
          entity.save
          entity.getObjectId should not equal (null)
        }

        it("should update entity") {
          entity.title = "Titulo Original"
          entity.save
          val objectId01 = entity.getObjectId
          entity.title = "Titulo Alterado"
          entity.save
          entity.getObjectId should equal(objectId01)
        }

      }

      describe("delete") {

        it("should delete entity") {

        }

      }

    }

  }

}