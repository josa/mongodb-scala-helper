package br.com.gfuture.mongodbhelper.model

import dao.converter.ObjectConverter

class EntityTest() extends Entity[EntityTest]{

  var title:String = null
  var description:String = null
  var transient:String = null

  transientFields += "transient"

  override def getConverter(): ObjectConverter[EntityTest] = {
      new ObjectConverter[EntityTest](classOf[EntityTest])
  }

}