package br.com.gfuture.mongodbhelper.model

import dao.converter.ObjectConverter
import com.mongodb.DBObject
import com.novus.casbah.mongodb.MongoDBObject
import java.lang.reflect.Field

trait Entity[T] {

  val transientFields = scala.collection.mutable.Set.empty[String]

  def getConverter(): ObjectConverter[T];

  def toDBObject(): DBObject = {
    val builder = MongoDBObject.newBuilder
    getClass.getDeclaredFields.foreach {
      field =>
        if (validateField(field)) {
          field.setAccessible(true)
          builder += field.getName -> field.get(this)
        }
    }
    builder.result
  }

  def validateField(field:Field):Boolean = {
    //Fields transients
    !transientFields.contains(field.getName)
  }

}