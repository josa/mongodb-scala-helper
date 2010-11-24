package br.com.gfuture.mongodbhelper.model

/**
 * Entidade de teste
 *
 * User: Jeosadache Galvão
 * Date: Nov 21, 2010
 * Time: 1:49:42 PM
 */
class EntityTest(title: String) extends Document {

  /**
   * define o nome da coleção onde o objeto será gravado
   */
  override def collectionName = "document_test"

  /**
   * Retorna o título do objeto
   */
  def getTitle = this.title
}