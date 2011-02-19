package br.com.gfuture.mongodbhelper

import mongodb.MongoProvider
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, BeforeAndAfterEach}
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId
import com.mongodb.DBObject
import java.lang.reflect.Field
import util._
import java.lang.String

class EntitySpec extends Spec with ShouldMatchers with BeforeAndAfterEach {

  var dbObject: DBObject = null

  val collectionName: String = "entitspec_test"

  override def beforeEach {
    dbObject = MongoDBObject("_id" -> org.bson.types.ObjectId.get, "title" -> "Título")
  }

  override def afterEach {
    MongoProvider.getCollection(collectionName).drop
  }

  describe("Entity") {

    describe("Persistencia") {

      it("deveria salvar um dbObject no mongo") {
        val mongoId: ObjectId = Entity.save(dbObject, collectionName)
        mongoId should not equal (null)
      }

      it("deveria atualizar um dbObject no mongo") {
        val novoTitulo: String = "Novo Titulo"
        val mongoId: ObjectId = Entity.save(dbObject, collectionName)

        dbObject.put("title", novoTitulo)
        Entity.save(dbObject, collectionName)

        val one: DBObject = MongoProvider.getCollection(collectionName).findOne(mongoId)
        one.get("title") should equal(novoTitulo)
      }

      it("deveria excluir um dbObject no mongo") {
        val mongoId: ObjectId = Entity.save(dbObject, collectionName)
        Entity.delete(mongoId, collectionName)
        MongoProvider.getCollection(collectionName).findOne(mongoId) should equal(null)
      }

    }

    describe("Fields") {

      it("deveria retornar o field _id da classe SubClass1") {
        val subClass = new SubClass1()
        val field: Field = Entity.findField("_id", subClass.getClass)
        field should not equal (null)
      }

      it("deveria retornar o field _id da classe SubClass2") {
        val subClass = new SubClass2()
        val field: Field = Entity.findField("_id", subClass.getClass)
        field should not equal (null)
      }

      it("deveria retornar o field _id da classe SubClass3") {
        val subClass = new SubClass3()
        val field: Field = Entity.findField("_id", subClass.getClass)
        field should not equal (null)
      }

      it("deveria retornar o field _id da classe SubClass4") {
        val subClass = new SubClass4()
        val field: Field = Entity.findField("_id", subClass.getClass)
        field should not equal (null)
      }

      it("deveria lançar exceção caso pesquise por um field que não exista") {
        val subClass1 = new SubClass1()
        evaluating {
          Entity.findField("abacateazul", subClass1.getClass)
        } should produce[RuntimeException]
      }

    }

    describe("equals") {

      it("deveria comparar dois objetos") {
        val object1 = new EntityTest()
        object1._id = org.bson.types.ObjectId.get
        val object2 = new EntityTest()
        object2._id = object1._id
        object1.equals(object2) should equal(true)
      }

    }

  }

}