package br.com.gfuture.mongodbhelper

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}

class QuerySpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  var entity:EntityTest = null

  override def beforeEach(){
    entity = new EntityTest
    entity.title = "Entidade para pesquisa"
    entity.save
  }

  describe("br.com.gfuture.mongodbhelper.Query"){

    it("should find by id"){
      pending
      val query = new Query[EntityTest](classOf[EntityTest]);
      val entityFind = query.findById(entity.getObjectId)
      entityFind should equal(entity)
    }

  }

}