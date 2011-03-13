package br.com.gfuture.mongodbhelper

class Reference(private val document: Document) extends Association {

  override def getAssociationType = AssociationType.Reference

  override def getDocument = this.document

}