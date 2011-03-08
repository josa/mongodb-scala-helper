package br.com.gfuture.mongodbhelper

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}
import util._

class QuerySpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  var entity: EntityTest = null
  var entityNivel1: SubClass1 = null
  var entityNivel2: SubClass1 = null
  var entityNivel3: SubClass1 = null
  var entityNivel4: SubClass1 = null

  override def beforeEach() {

    entity = new EntityTest
    entity.title = "Entidade para pesquisa"
    entity.save

    entityNivel1 = new SubClass1
    entityNivel1.title = "Entidade Nivel 1"
    entityNivel1.save

    entityNivel2 = new SubClass2
    entityNivel2.title = "Entidade Nivel 2"
    entityNivel2.save

    entityNivel3 = new SubClass3
    entityNivel3.title = "Entidade Nivel 3"
    entityNivel3.save

    entityNivel4 = new SubClass4
    entityNivel4.title = "Entidade Nivel 4"
    entityNivel4.save

  }

  override def afterEach() {
    entity.delete
    entityNivel1.delete
  }

  describe("DocumentManager") {

    describe("findById") {

      it("deveria buscar pelo id") {
        val DocumentManager = new DocumentManager[EntityTest](classOf[EntityTest]);
        val entityFind = DocumentManager.findById(entity.getObjectId)
        entityFind should equal(entity)
      }

      it("deveria incluir o objectId no documento(entidade)"){
        val DocumentManager = new DocumentManager[EntityTest](classOf[EntityTest]);
        val entityFind = DocumentManager.findById(entity.getObjectId)
        entityFind.getObjectId should not equal(null)
      }

      it("deveria retornar nulo para id inexistente") {
        val query = new DocumentManager[EntityTest](classOf[EntityTest]);
        val entityFind = query.findById(org.bson.types.ObjectId.get)
        entityFind should equal(null)
      }

      it("deveria consultar subclasse nivel 1") {
        val query = new DocumentManager[SubClass1](classOf[SubClass1]);
        val entityFind = query.findById(entityNivel1.getObjectId)
        entityFind should equal(entityNivel1)
      }

      it("deveria consultar subclasse nivel 2") {
        val query = new DocumentManager[SubClass2](classOf[SubClass2]);
        val entityFind = query.findById(entityNivel2.getObjectId)
        entityFind should equal(entityNivel2)
      }

      it("deveria consultar subclasse nivel 3") {
        val query = new DocumentManager[SubClass3](classOf[SubClass3]);
        val entityFind = query.findById(entityNivel3.getObjectId)
        entityFind should equal(entityNivel3)
      }


      it("deveria consultar subclasse nivel 4") {
        val query = new DocumentManager[SubClass4](classOf[SubClass4]);
        val entityFind = query.findById(entityNivel4.getObjectId)
        entityFind should equal(entityNivel4)
      }

    }

    describe("dinamic find"){
      val documentManager = new DocumentManager[EntityTest](classOf[EntityTest]);
      val uniqueResult = documentManager.createQuery.addClause("title", "Entidade para pesquisa").uniqueResult
      uniqueResult.title should equal("Entidade para pesquisa")
      uniqueResult._id should not equal(null)
    }

  }

}