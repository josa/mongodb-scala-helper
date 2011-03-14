package br.com.gfuture.mongodbhelper

import annotations.{DocElement, AssociationType, Reference, CascadeType}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}
import com.mongodb.casbah.commons.MongoDBObject

class CategoryExample extends Document {

  @DocElement
  @Reference(association = AssociationType.ONE_TO_ONE, cascade = CascadeType.SAVE)
  var parent: CategoryExample = null

}

class DocumentReferenceSpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  describe("Association") {

    describe("Reference") {

      describe("marshall") {

        it("should include reference to DBObject") {

          val category = new CategoryExample
          category.save

          val parentCategory = new CategoryExample
          parentCategory.save

          category.parent = parentCategory

          val dbObject = Document.toDBObject(category)
          dbObject should equal(MongoDBObject("_id" -> category.getObjectId, "parent" -> parentCategory.getObjectId))

          category.delete
          parentCategory.delete

        }

      }

      describe("unmarshall") {

        val category = new CategoryExample
        category.save

        val parentCategory = new CategoryExample
        parentCategory.save

        category.parent = parentCategory

        val bObject = MongoDBObject("_id" -> category.getObjectId, "parent" -> parentCategory.getObjectId)

        val document = Document.fromMongoObject(bObject, classOf[CategoryExample])

        document.parent should equal(parentCategory)

      }

    }

  }

}