package br.com.gfuture.mongodbhelper.util

import br.com.gfuture.mongodbhelper.Entity
import collection.mutable.Set

class EntityTest extends Entity {

  var title: String = null
  var description: String = null
  var transient: String = null

  override def getTransientFields:Set[String] ={
    super.getTransientFields += "transient"
  }

  override def equals(that: Any) = that match {
    case other: EntityTest => other.getClass == getClass && other.getObjectId.equals(getObjectId)
    case _ => false
  }

  override def toString = "EntityTest[_id="+getObjectId+"]"

}