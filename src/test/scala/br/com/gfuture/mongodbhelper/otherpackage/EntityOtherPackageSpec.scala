/*
 * Created by IntelliJ IDEA.
 * User: josa
 * Date: 2/17/11
 * Time: 6:27 PM
 */
package br.com.gfuture.mongodbhelper.otherpackage

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}

class EntityOtherPackageSpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  describe("EntityOtherPackage") {

    it("should save entity in other package") {
      val entityOther = new EntityOtherPackage()
      entityOther.save
      entityOther.getObjectId should not equal (null)
    }

  }

}