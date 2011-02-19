package br.com.gfuture.mongodbhelper

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}
import util.{SubClass1, EntityTest}

class QuerySpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  var entity: EntityTest = null
  var entityNivel1: SubClass1 = null


  override def beforeEach() {

    entity = new EntityTest
    entity.title = "Entidade para pesquisa"
    entity.save

    entityNivel1 = new SubClass1
    entityNivel1.title = "Entidade Nivel 1"
    entityNivel1.save

  }

  override def afterEach() {
    entity.delete
    entityNivel1.delete
  }

  describe("Query") {

    it("deveria buscar pelo id") {
      val query = new Query[EntityTest](classOf[EntityTest]);
      val entityFind = query.findById(entity.getObjectId)
      entityFind should equal(entity)
    }

    it("deveria retornar nulo para id inexistente") {
      val query = new Query[EntityTest](classOf[EntityTest]);
      val entityFind = query.findById(org.bson.types.ObjectId.get)
      entityFind should equal(null)
    }

    //TODO testar subclasses
    it("deveria consultar subclasse nivel 1") {
      val query = new Query[SubClass1](classOf[SubClass1]);
      val entityFind = query.findById(entityNivel1.getObjectId)
      entityFind should equal(entityNivel1)
    }

  }

}