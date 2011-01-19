package br.com.gfuture.mongodbhelper.model

import dao.converter.ObjectConverter

trait Entity[T]{

  def getConverter(): ObjectConverter[T];

}