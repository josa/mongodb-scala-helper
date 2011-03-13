package br.com.gfuture.mongodbhelper

import annotations.DocElement
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}

class QuerySpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  var document: DocumentExample = null

  override def beforeEach() {
    document = new DocumentExample
    document.valueOne = "value for search"
    document.save
  }

  override def afterEach() {
    document.delete
  }

  describe("DocumentManager") {

    describe("findById") {

      it("should search by id") {
        val DocumentManager = new DocumentManager[DocumentExample](classOf[DocumentExample]);
        val entityFind = DocumentManager.findById(document.getObjectId)
        entityFind should equal(document)
      }

      it("should add objectId in document") {
        val DocumentManager = new DocumentManager[DocumentExample](classOf[DocumentExample]);
        val entityFind = DocumentManager.findById(document.getObjectId)
        entityFind.getObjectId should not equal (null)
      }

      it("should return null for id not found") {
        val query = new DocumentManager[DocumentExample](classOf[DocumentExample]);
        val entityFind = query.findById(org.bson.types.ObjectId.get)
        entityFind should equal(null)
      }

    }

    describe("dinamic find") {

      it("should return objectList") {
        val documentManager = new DocumentManager[DocumentExample](classOf[DocumentExample]);
        val uniqueResult = documentManager.createQuery.addClause("valueOne", "value for search").uniqueResult
        uniqueResult should not equal (null)
        uniqueResult.valueOne should equal("value for search")
        uniqueResult.getObjectId should not equal (null)
      }

    }

  }

}