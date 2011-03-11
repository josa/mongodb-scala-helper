package br.com.gfuture.mongodbhelper

object AssociationType extends Enumeration {

  type AssociationType = Value

  val Reference = Value

}

/**Trait responsável por implementar uma interface de associação entre documentos mongo
 *
 */
trait Association {

  import AssociationType._

  def getDocument: Document

  def getAssociationType: AssociationType

}