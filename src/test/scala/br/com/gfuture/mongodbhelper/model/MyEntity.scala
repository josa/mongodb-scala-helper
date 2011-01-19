package br.com.gfuture.mongodbhelper.model

import dao.converter.ObjectConverter

class MyEntity extends Entity[MyEntity]{

  override def getConverter(): ObjectConverter[MyEntity] = {
      new ObjectConverter[MyEntity](classOf[MyEntity])
  }

}