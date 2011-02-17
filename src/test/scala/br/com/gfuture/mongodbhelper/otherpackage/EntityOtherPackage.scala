package br.com.gfuture.mongodbhelper.otherpackage

import br.com.gfuture.mongodbhelper.Entity

class EntityOtherPackage extends Entity {

  var title: String = null

  override def equals(that: Any) = that match {
    case other: EntityOtherPackage => other.getClass == getClass && other.getObjectId.equals(getObjectId)
    case _ => false
  }

  override def toString = "EntityOtherPackage[_id=" + getObjectId + "]"

}