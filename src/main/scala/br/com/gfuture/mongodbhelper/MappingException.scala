package br.com.gfuture.mongodbhelper

class MappingException(val message: String) extends Exception {

  override def getMessage = this.message

}