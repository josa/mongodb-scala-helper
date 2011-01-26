package br.com.gfuture.mongodbhelper.converter

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}
import br.com.gfuture.mongodbhelper.EntityTest

class ObjectConverterSpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  describe("MongoDao") {

    it("should generate simple object"){
      val objectConverter = new ObjectConverter[EntityTest](classOf[EntityTest])
      objectConverter.getTypeInstance.getClass should equal(classOf[EntityTest])
    }

  }

}