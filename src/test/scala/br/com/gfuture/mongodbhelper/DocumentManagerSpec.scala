package br.com.gfuture.mongodbhelper

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}

class DocumentRefForManager extends Document(classOf[DocumentRefForManager]) {

}

import org.junit.Test

import org.junit.runner.RunWith

@RunWith(classOf[org.scalatest.junit.JUnitRunner])
class DocumentManagerSpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  @Test
  def test_it = execute() 
  
  describe("constructor") {

    it("should generate new instance") {
      val manager = new DocumentManager(classOf[DocumentRefForManager])
      manager should not equal (null)
    }

    it("should generate new query") {
      val manager = new DocumentManager(classOf[DocumentRefForManager])
      val query:Query = manager.createQuery
      query should not equal(null)
    }

  }

}