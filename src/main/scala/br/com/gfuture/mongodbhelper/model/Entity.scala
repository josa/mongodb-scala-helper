package br.com.gfuture.mongodbhelper.model

import dao.converter.ObjectConverter
import com.mongodb.DBObject
import com.novus.casbah.mongodb.MongoDBObject

trait Entity[T] {

  def getConverter(): ObjectConverter[T];

  def toDBObject(): DBObject = {
    val builder = MongoDBObject.newBuilder
    getClass.getDeclaredFields.foreach {
      field =>
        field.setAccessible(true)
        builder += field.getName -> field.get(this)
    }
    builder.result
  }

}