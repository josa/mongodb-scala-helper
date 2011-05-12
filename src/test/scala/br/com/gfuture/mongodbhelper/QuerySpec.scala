package br.com.gfuture.mongodbhelper

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}
import org.junit.Test

import org.junit.runner.RunWith

@RunWith(classOf[org.scalatest.junit.JUnitRunner])
class QuerySpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  @Test
  def test_it = execute() 
  
  var document: DocumentExample = null
  var documentTwo: DocumentExample = null
  
  override def beforeEach() {
    document = new DocumentExample
    document.valueOne = "value for search"
    document.save
    
    documentTwo = new DocumentExample
    documentTwo.valueOne = "value for search 2"
    documentTwo.save
  }

  override def afterEach() {
    document.delete
    documentTwo.delete
  }

  describe("DocumentManager") {

    describe("findById") {

      it("should search by id") {
        val DocumentManager = new DocumentManager(classOf[DocumentExample]);
        val entityFind = DocumentManager.findById(document.getObjectId)
        entityFind should equal(document)
      }

      it("should add objectId in document") {
        val DocumentManager = new DocumentManager(classOf[DocumentExample]);
        val entityFind = DocumentManager.findById(document.getObjectId)
        entityFind.getObjectId should not equal (null)
      }

      it("should return null for id not found") {
        val query = new DocumentManager(classOf[DocumentExample]);
        val entityFind = query.findById(org.bson.types.ObjectId.get)
        entityFind should equal(null)
      }

    }

    describe("dinamic find") {

      it("should return objectList") {
        val documentManager = new DocumentManager(classOf[DocumentExample]);
        val uniqueResult = documentManager.createQuery.addClause("valueOne", "value for search").uniqueResult.asInstanceOf[DocumentExample]
        uniqueResult should not equal (null)
        uniqueResult.valueOne should equal("value for search")
        uniqueResult.getObjectId should not equal (null)
      }

    }
    
    describe("find"){
      
      it("should find all"){
        val documentManager = new DocumentManager(classOf[DocumentExample])
        val resultList = documentManager.findAll
        resultList.size should equal(2)
      }
      
    }
    
    describe("delete"){
      
      it("should delete all"){
        val documentManager = new DocumentManager(classOf[DocumentExample])
        val doc = new DocumentExample
        doc.valueOne = "bla"
        doc.save
        documentManager.deleteAll
        documentManager.findAll.size should equal(0)
      }
      
    }

  }

}