package br.com.gfuture.mongodbhelper.model

import dao.converter.ObjectConverter

class EntityTest() extends Entity[EntityTest]{

  var title:String = null
  var description:String = null

  override def getConverter(): ObjectConverter[EntityTest] = {
      new ObjectConverter[EntityTest](classOf[EntityTest])
  }

}