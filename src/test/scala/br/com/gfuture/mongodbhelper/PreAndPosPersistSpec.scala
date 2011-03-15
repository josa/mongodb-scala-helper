package br.com.gfuture.mongodbhelper

import annotations.{PosPersist, PrePersist}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}
import org.scalatest.mock.MockitoSugar

class PreAndPosPersistExample extends Document[PreAndPosPersistExample](classOf[PreAndPosPersistExample]) {

  var checkPrePersist = false
  var checkPosPersist = false

  @PrePersist
  def prePersist() {
    this.checkPrePersist = true
  }

  @PosPersist
  def posPersist() {
    this.checkPosPersist = true
  }

}

class PreAndPosPersistSpec extends Spec with ShouldMatchers with BeforeAndAfterEach with MockitoSugar {

  var prePersistExample: PreAndPosPersistExample = null;

  override def beforeEach {
    prePersistExample = new PreAndPosPersistExample
  }

  describe("PrePersist") {
    it("should call prepersist") {
      MongoDocumentHelper.callPrePersist(prePersistExample)
      prePersistExample.checkPrePersist should equal(true)
    }
  }

  describe("PosPersist") {
    it("should call pospersist") {
      MongoDocumentHelper.callPosPersist(prePersistExample)
      prePersistExample.checkPosPersist should equal(true)
    }
  }

}