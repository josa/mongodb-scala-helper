package br.com.gfuture.mongodbhelper.reflect

import java.lang.reflect.Field

/**Utilitário para trabalhar com reflexão
 */
object ReflectUtil {

  /**Carrega os fields da classe e superclasses recusivamente
   *
   * @param a classe
   *
   */
  def loadFieldsRecursively(documentClass: Class[_]): List[Field] = {
    loadFieldsRecursively(documentClass, List.empty[Field])
  }

  /**Carrega os fields da classe e superclasses recusivamente
   *
   * @param a classe
   * @param a lista de fields
   *
   */
  def loadFieldsRecursively(documentClass: Class[_], fieldList: List[Field]): List[Field] = {
    documentClass match {
      case c: Class[_] =>
        loadFieldsRecursively(documentClass.getSuperclass, fieldList union c.getDeclaredFields.toList)
      case _ =>
        fieldList
    }
  }

  /**Pesquisa um field da classe e superclasses reculsirvamente
   *
   * @param o nome do field
   * @param a classe da entidade
   *
   */
  def findField(name: String, documentClass: Class[_]): Field = {
    try {
      documentClass.getDeclaredField(name)
    } catch {
      case e: java.lang.NoSuchFieldException =>
        documentClass.getSuperclass match {
          case x: Class[_] =>
            findField(name, documentClass.getSuperclass)
          case _ =>
            throw new RuntimeException("field not found: " + documentClass.getName + "[" + name + "]")
        }

    }
  }


  /**Chama um método anotado pela anotação passada como parametro
   */
  def callAnnotatedMethod[T](obj: AnyRef, annotation: Class[_ <: java.lang.annotation.Annotation]) {
    callAnnotatedMethodRecursively(obj, obj.getClass, annotation)
  }

  /**Chama um método anotado recusivamente
   */
  private def callAnnotatedMethodRecursively[T](obj: AnyRef, clazz: Class[_], annotation: Class[_ <: java.lang.annotation.Annotation]) {
    clazz match {
      case c: Class[_] =>
        clazz.getMethods.foreach({
          m =>
            if (m.isAnnotationPresent(annotation)) {
              m.invoke(obj)
              return
            }
        })
      case _ =>
        return
    }
    callAnnotatedMethodRecursively(obj, clazz.getSuperclass, annotation)
  }

}