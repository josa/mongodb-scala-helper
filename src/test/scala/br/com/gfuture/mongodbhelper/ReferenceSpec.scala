package br.com.gfuture.mongodbhelper

import annotations.{DocElement, CascadeType, AssociationType, Reference}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}
import com.mongodb.casbah.commons.MongoDBObject

class BaseExample extends Document(classOf[BaseExample]) {

  @DocElement
  @Reference(association = AssociationType.ONE_TO_ONE, cascade = CascadeType.SAVE)
  var parent: ParentExample = null

}

class ParentExample extends Document(classOf[ParentExample]) {

}

class ReferenceSpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  var base = null.asInstanceOf[BaseExample]
  var parent = null.asInstanceOf[ParentExample]

  override def beforeEach {
    base = new BaseExample
    base.save

    parent = new ParentExample
    parent.save

    base.parent = parent
  }

  override def afterEach {
    base.delete
    parent.delete
  }

  describe("Reference") {
    it("should unmarshall reference") {
      val baseUnmarshall = DocumentTools.fromMongoObject(MongoDBObject("parent" -> parent.getObjectId), classOf[BaseExample]).asInstanceOf[BaseExample]
      baseUnmarshall.parent should equal(parent)
    }

    it("should marshall reference") {
      val dbObject = MongoDBObject("_id" -> base.getObjectId, "parent" -> parent.getObjectId)
      DocumentTools.toDBObject(base) should equal(dbObject)
    }

    it("should save in cascade marshall") {
      base.parent = new ParentExample
      val dbObject = DocumentTools.toDBObject(base)
      dbObject.get("parent") should not equal(null)
      dbObject.get("parent") should equal(base.parent.getObjectId)
    }

  }

}