package br.com.gfuture.mongodbhelper

class PersistenceException(val message: String) extends Exception {

  override def getMessage = this.message

}