package br.com.gfuture.mongodbhelper

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}
import br.com.gfuture.mongodbhelper.mongodb.MongoProvider
import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(classOf[org.scalatest.junit.JUnitRunner])
class DocumentSpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  @Test
  def test_it = execute() 
    
  var document = null.asInstanceOf[DocumentExample]

  var dbObject = null.asInstanceOf[DBObject]

  override def beforeEach() {
    document = new DocumentExample
    document.valueOne = "value one"
    document.valueTransient = "not included"
    dbObject = MongoDBObject("valueOne" -> "value one")
  }
  
  override def afterEach(){
    MongoProvider.getCollection(document.getClass).remove(new BasicDBObject())
  }

  describe("equals") {

    it("deveria comparar dois objetos") {
      val object1 = new DocumentExample()
      object1.setObjectId(org.bson.types.ObjectId.get)
      val object2 = new DocumentExample()
      object2.setObjectId(object1.getObjectId)
      object1.equals(object2) should equal(true)
    }
    
  }

  describe("marshall") {

    it("the _id should be null before persisting") {
      DocumentTools.toDBObject(document).get("_id") should equal(null)
    }

    it("should generate mongodbObject") {
      DocumentTools.toDBObject(document).get("valueOne") should equal("value one")
    }

    it("should generate a mongodbObject with only two element") {
      DocumentTools.toDBObject(document).keySet.size should equal(2)
    }

    it("should not include valueTransient") {
      DocumentTools.toDBObject(document).get("valueTransient") should equal(null)
    }

  }

  describe("unmarshall") {

    it("should generate object from mongodbObject") {
      val objectFrom = DocumentTools.fromMongoObject(dbObject, classOf[DocumentExample].asInstanceOf[Class[Document]])
      objectFrom.asInstanceOf[DocumentExample].valueOne should equal("value one")
    }

    it("should not load transientValue") {
      val objectFrom = DocumentTools.fromMongoObject(dbObject, classOf[DocumentExample])
      objectFrom.asInstanceOf[DocumentExample].valueTransient should equal(null)
    }

  }

  describe("persistence") {

    def findInMongo(id: ObjectId): DBObject = {
      MongoProvider.getCollection(document.getClass).findOne(MongoDBObject("_id" -> id))
    }

    it("should save object") {
      document.save
      findInMongo(document.getObjectId) should not equal (null)
    }

    it("should update object") {
      document.save
      document.valueOne = "value updated"
      document.save
      findInMongo(document.getObjectId).get("valueOne") should equal("value updated")
    }

    it("should delete object") {
      document.save
      document.delete
      findInMongo(document.getObjectId) should equal(null)
    }

  }

}