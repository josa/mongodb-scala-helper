package br.com.gfuture.mongodbhelper.reflect

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}
import br.com.gfuture.mongodbhelper.Document
import br.com.gfuture.mongodbhelper.annotations.PrePersist


class DocumentForReflectUtil extends Document(classOf[DocumentForReflectUtil]) {

  var qtd: Int = 0

  @PrePersist
  def prePersist() {
    qtd = qtd + 1
  }

}

class SubDocument extends DocumentForReflectUtil{

  @PrePersist
  def prePersistSub() {
    qtd = qtd + 1
  }

}

class ReflectUtilSpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  describe("ReflectUtil") {

    it("should throw exception if field not found") {
      val document = new DocumentForReflectUtil
      evaluating {
        ReflectUtil.findField("fieldNotFound", document.getClass)
      } should produce[RuntimeException]
    }

    it("should call annoted method") {
      val document = new DocumentForReflectUtil
      ReflectUtil.callAnnotatedMethod(document, classOf[PrePersist])
      document.qtd should equal(1)
    }

    it("should only calls the first annoted method") {
      val document = new SubDocument
      ReflectUtil.callAnnotatedMethod(document, classOf[PrePersist])
      document.qtd should equal(1)
    }

  }
}