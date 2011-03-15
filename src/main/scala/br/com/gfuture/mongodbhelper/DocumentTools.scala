package br.com.gfuture.mongodbhelper

import annotations.{CascadeType, Reference, AssociationType, DocElement}
import com.mongodb.DBObject
import reflect.ReflectUtil
import org.bson.types.ObjectId
import java.lang.reflect.Field
import org.slf4j.LoggerFactory
import com.mongodb.casbah.commons.MongoDBObject

/**Ferramentas de apoio a manipulação dos documentos
 */
object DocumentTools {

  protected lazy val logger = LoggerFactory.getLogger(DocumentTools.getClass)

  /**Converte o objeto em um objeto de persistencia do mongo
   *
   * @param document, a entidade a ser convertida
   * @return uma instancia da classe com.mongodb.DBObject
   */
  def toDBObject(document: Document): DBObject = {

    val builder = MongoDBObject.newBuilder

    def processAnnotationsAndGenerateElement(field: Field): (String, AnyRef) = {
      field.getAnnotation(classOf[Reference]) match {
        case refAnnotation: Reference =>
          val documentReference = field.get(document).asInstanceOf[Document]
          if (!refAnnotation.cascade.equals(CascadeType.SAVE) && documentReference.getObjectId == null)
            throw new MappingException(field.getName + "CascadeType.NONE using the objectId is required")

          documentReference.getObjectId match {
            case o: ObjectId =>
              field.getName -> documentReference.getObjectId
            case null =>
              documentReference.save
              field.getName -> documentReference.getObjectId
          }

        case null =>
          field.getName -> field.get(document)
      }
    }

    ReflectUtil.loadFieldsRecursively(document.documentClass).foreach {
      field =>
        field.setAccessible(true)
        if (field.isAnnotationPresent(classOf[DocElement]) && field.get(document) != null)
          builder += processAnnotationsAndGenerateElement(field)
        else {
          if (logger.isDebugEnabled) {
            if (!field.isAnnotationPresent(classOf[DocElement]))
              logger.debug("toObject[field excluded(%s), @DocElement not found]" format (field.getName))
            if (field.get(document) != null)
              logger.debug("toObject[field excluded(%s), is null]" format (field.getName))
          }
        }
    }

    builder += "_id" -> document.getObjectId
    builder.result
  }

  /**
   * Converte o _id em um objeto de persistencia do mongo
   *
   * @param _id, instancia da classe org.bson.types.ObjectId
   * @return uma instancia da classe com.mongodb.DBObject
   */
  def toOBObject(objectId: org.bson.types.ObjectId): DBObject = {
    MongoDBObject("_id" -> objectId)
  }

  def fromMongoObject(dbObject: DBObject, documentClass: Class[_ <: Document]): Document = dbObject match {
    case dbObjectMatch: Any =>
      def processAnnotationsAndGetProperty(field: Field): AnyRef = {
        field.getAnnotation(classOf[Reference]) match {
          case refAnnotation: Reference =>
            if (refAnnotation.association.equals(AssociationType.ONE_TO_ONE)) {
              val reference = new DocumentManager(field.getType.asInstanceOf[Class[Document]]).findById(dbObject.get(field.getName).asInstanceOf[ObjectId])
              reference
            } else {
              null.asInstanceOf[AnyRef]
            }
          case _ =>
            dbObject.get(field.getName)
        }
      }
      val document = documentClass.newInstance
      ReflectUtil.loadFieldsRecursively(documentClass).foreach {
        field =>
          field.setAccessible(true)
          if (field.isAnnotationPresent(classOf[DocElement])) {
            field.set(document, processAnnotationsAndGetProperty(field))
          }
      }
      document.setObjectId(dbObject.get("_id").asInstanceOf[ObjectId])
      document
    case _ =>
      null.asInstanceOf[Document]
  }

}