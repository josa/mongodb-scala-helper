package br.com.gfuture.mongodbhelper.model.dao

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, Spec}

class MongoDaoSpec extends Spec with ShouldMatchers with BeforeAndAfterEach {
  describe("MongoDao") {
    describe("save") {

      /**
      it("should save simple object") {
        val simpleObject = new EntityTest("Simple Object")
        val dao = new MongoDao[EntityTest]
        dao.save(simpleObject)
        simpleObject.getObjectId should not equal (null)
      }

      it("should find by object") {
        val simpleObject = new EntityTest("Simple Object")
        val dao = new MongoDao[EntityTest]
        dao.save(simpleObject)
        val objectForMongo = dao.findByObjectId(simpleObject.getObjectId)
        objectForMongo.getObjectId should equal(simpleObject.getObjectId)
      }
      **/

    }
  }
}