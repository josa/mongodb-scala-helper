package br.com.gfuture.mongodbhelper.reflect

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}
import br.com.gfuture.mongodbhelper.Document


class DocumentForReflectUtil extends Document(classOf[DocumentForReflectUtil]){

}

class ReflectUtilSpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  describe("ReflectUtil") {

    it("should throw exception if field not found") {
      val document = new DocumentForReflectUtil
      evaluating {
        ReflectUtil.findField("fieldNotFound", document.getClass)
      } should produce[RuntimeException]
    }

  }
}