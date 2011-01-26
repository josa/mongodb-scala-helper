package br.com.gfuture.mongodbhelper.converter

/**
 * Created by IntelliJ IDEA.
 * User: jeosadache
 * Date: Dec 4, 2010
 * Time: 8:23:36 PM
 * To change this template use File | Settings | File Templates.
 */

class ObjectConverter[T](tipo: Class[T]) {

  def getTypeInstance = {
    tipo.newInstance
  }

}