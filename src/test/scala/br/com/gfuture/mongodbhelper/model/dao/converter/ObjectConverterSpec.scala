package br.com.gfuture.mongodbhelper.model.dao.converter

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}
import br.com.gfuture.mongodbhelper.model.EntityTest

/**
 * Created by IntelliJ IDEA.
 * User: jeosadache
 * Date: Dec 4, 2010
 * Time: 8:20:39 PM
 * To change this template use File | Settings | File Templates.
 */

class ObjectConverterSpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  describe("MongoDao") {

    it("should generate simple object"){
      val objectConverter = new ObjectConverter[EntityTest](classOf[EntityTest])
      objectConverter.getTypeInstance.getClass should equal(classOf[EntityTest])
    }

  }

}