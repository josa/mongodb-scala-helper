package br.com.gfuture.mongodbhelper.util

import br.com.gfuture.mongodbhelper.Entity

class EntityTest extends Entity {

  var title: String = null
  var description: String = null
  var transient: String = null

  transientFields += "transient"

  override def equals(that: Any) = that match {
    case other: EntityTest => other.getClass == getClass && other.getObjectId.equals(getObjectId)
    case _ => false
  }

  override def toString = "EntityTest[_id="+getObjectId+"]"

}