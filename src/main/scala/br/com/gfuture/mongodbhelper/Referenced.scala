package br.com.gfuture.mongodbhelper

class Reference(val document: Document) extends Association {

  override def getAssociationType = AssociationType.Reference

  override def getDocument: Document = this.document

}