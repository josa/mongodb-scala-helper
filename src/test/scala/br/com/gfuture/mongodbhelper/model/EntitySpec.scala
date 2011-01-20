package br.com.gfuture.mongodbhelper.model

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}

class EntitySpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  var entity = new EntityTest

  /**
   *
      #	deleted:    src/main/scala/br/com/gfuture/mongodbhelper/model/EntityTest.scala
      #	deleted:    src/test/scala/br/com/gfuture/mongodbhelper/model/DocumentSpec.scala
      #	deleted:    src/test/scala/br/com/gfuture/mongodbhelper/model/MyEntity.scala
      #	deleted:    target/scala_2.8.0/classes/br/com/gfuture/mongodbhelper/model/EntityTest.class
      #	deleted:    target/scala_2.8.0/test-classes/br/com/gfuture/mongodbhelper/model/dao/converter/MyEntity.class

   */

  describe("EntitySpec") {

    describe("load") {

      it("should generate DBObject with title") {
        entity.title = "My Title"
        val dbObject = entity.toDBObject
        dbObject.get("title") should equal("My Title")
      }

      it("should generate DBObject with description") {
        entity.description = "My Description"
        val dbObject = entity.toDBObject
        dbObject.get("description") should equal("My Description")
      }

    }

  }

}