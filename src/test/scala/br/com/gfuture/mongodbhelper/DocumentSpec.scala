package br.com.gfuture.mongodbhelper

import annotations.DocElement
import mongodb.MongoProvider
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId

class DocumentExample extends Document {

  @DocElement
  var valueOne: String = null

  var valueTransient: String = null

  override def equals(that: Any) = that match {
    case other: DocumentExample => other.getClass == getClass && other.getObjectId.equals(getObjectId)
    case _ => false
  }

}

class EspecializedDocumentExample extends DocumentExample {

}

class ObjectDocumentSpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  var document: DocumentExample = null
  var dbObject: DBObject = null

  override def beforeEach {
    document = new DocumentExample
    document.valueOne = "value one"
    document.valueTransient = "not included"

    dbObject = MongoDBObject("valueOne" -> "value one")
  }

  override def afterEach {
    document.delete
  }

  describe("marshall") {

    it("the _id should be null before persisting") {
      Document.toMongoObject(document).get("_id") should equal(null)
    }

    it("should generate mongodbObject") {
      Document.toMongoObject(document).get("valueOne") should equal("value one")
    }

    it("should generate a mongodbObject with only two element") {
      Document.toMongoObject(document).keySet.size should equal(2)
    }

    it("should not include valueTransient") {
      Document.toMongoObject(document).get("valueTransient") should equal(null)
    }

    it("should generate mongodbObject in specialized document") {
      val especDocument = new EspecializedDocumentExample
      especDocument.valueOne = "value especialized"
      Document.toMongoObject(especDocument).get("valueOne") should equal("value especialized")
    }

  }

  describe("unmarshall") {

    it("should generate object from mongodbObject") {
      val objectFrom = Document.fromMongoObject(dbObject, classOf[DocumentExample])
      objectFrom.valueOne should equal("value one")
    }

    it("should not load transientValue") {
      val objectFrom = Document.fromMongoObject(dbObject, classOf[DocumentExample])
      objectFrom.valueTransient should equal(null)
    }

    it("should throw exception if field not found") {
      evaluating {
        Document.findField("fieldNotFound", document.getClass)
      } should produce[RuntimeException]
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

  describe("equals") {

    it("deveria comparar dois objetos") {
      val object1 = new DocumentExample()
      object1.setObjectId(org.bson.types.ObjectId.get)
      val object2 = new DocumentExample()
      object2.setObjectId(object1.getObjectId)
      object1.equals(object2) should equal(true)
    }

  }

}