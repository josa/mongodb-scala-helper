/*
 * Created by IntelliJ IDEA.
 * User: josa
 * Date: 2/17/11
 * Time: 7:42 PM
 */
package br.com.gfuture.mongodbhelper

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, Spec}
import java.lang.Class
;

class SubEntitySpec extends Spec with ShouldMatchers with BeforeAndAfterEach {


  describe("SubEntity") {

    it("test") {

      val subEntity = new SubEntity()
      val classes: Array[Class[_]] = subEntity.getClass.getClasses

      classes.


    }

  }


}