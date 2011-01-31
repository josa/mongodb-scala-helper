package br.com.gfuture.mongodbhelper

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}

class EntitySpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  var entity:EntityTest = null

  override def beforeEach() {
    entity = new EntityTest
  }

  describe("Entity") {

    describe("create"){

      it("should create instanceof EntityTest"){
        val create: Unit = Entity.create(classOf[br.com.gfuture.mongodbhelper.EntityTest])
        val entity:br.com.gfuture.mongodbhelper.EntityTest = (br.com.gfuture.mongodbhelper.EntityTest)create
      }

    }

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

    describe("persistence") {

      describe("save") {

        it("should save entity") {
          entity.title = "My Title"
          entity.save
          entity.getObjectId should not equal (null)
        }

        it("should update entity"){
          entity.title = "Titulo Original"
          entity.save
          val objectId01 = entity.getObjectId
          entity.title = "Titulo Alterado"
          entity.save
          entity.getObjectId should equal(objectId01)
        }

      }

      describe("delete"){

        it("should delete entity"){

        }

      }

    }

  }

}