package br.com.gfuture.mongodbhelper.util

import br.com.gfuture.mongodbhelper.{Reference, Document}

class Category extends Document {

  var name: String = null;
  var parent:Reference = null

  override def getTransientFields = Set.empty[String]

  override def equals(that: Any) = that match {
    case other: Category => other.getClass == getClass && other.getObjectId.equals(getObjectId)
    case _ => false
  }

}