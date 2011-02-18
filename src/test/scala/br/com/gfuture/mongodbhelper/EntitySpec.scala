package br.com.gfuture.mongodbhelper

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId
import com.mongodb.DBObject
import java.lang.reflect.Field
import util.{EntityTest, SubClass1}

class EntitySpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  var dbObject: DBObject = null

  override def beforeEach {
    dbObject = MongoDBObject("_id" -> org.bson.types.ObjectId.get, "title" -> "Título")
  }

  describe("Entity") {

    it("deveria salvar um dbObject no mongo") {
      val mongoId: ObjectId = Entity.save(dbObject, "abacate")
      mongoId should not equal (null)
    }

    it("deveria retornar o field _id da classe SubClass1") {
      val subClass1 = new SubClass1()
      val field: Field = Entity.findField("_id", subClass1.getClass)
      field should not equal (null)
    }

    it("deveria lançar exceção caso pesquise por um field que não exista") {
      val subClass1 = new SubClass1()
      evaluating {
        Entity.findField("abacateazul", subClass1.getClass)
      } should produce[RuntimeException]
    }

    describe("equals"){

        it("deveria testar dois objetos"){
          val object1 = new EntityTest()
          object1._id =  org.bson.types.ObjectId.get
          val object2 = new EntityTest()
          object2._id =  object1._id
          object1.equals(object2) should equal(true)
        }

    }


  }

}

