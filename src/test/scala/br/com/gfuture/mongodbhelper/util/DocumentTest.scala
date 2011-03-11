package br.com.gfuture.mongodbhelper.util

import br.com.gfuture.mongodbhelper.Document

class DocumentTest extends Document {

  var title: String = null
  var description: String = null
  var transient: String = null

  override def getTransientFields:Set[String] ={
    Set("transient")
  }

  override def equals(that: Any) = that match {
    case other: DocumentTest => other.getClass == getClass && other.getObjectId.equals(getObjectId)
    case _ => false
  }

}