package br.com.gfuture.mongodbhelper.configuration

import java.io.{InputStream, File, FileInputStream, IOException}
import org.slf4j.LoggerFactory

/**Carrega as configurações da aplicação
 *
 * @user jeosadache galvão, josa.galvao@gmail.com
 */
object Configuration extends Configuration {

  protected def propertyFile = "/mongodb-scala-helper.properties"

  protected def pickJarBasedOn = classOf[Configuration]

}

trait Configuration {

  private lazy val logger = LoggerFactory.getLogger(getClass)

  protected def propertyFile: String

  protected def pickJarBasedOn: Class[_]

  lazy val properties: java.util.Properties = loadProperties

  protected def loadProperties: java.util.Properties = {
    val props = new java.util.Properties
    var stream: InputStream = null
    System.getProperty("mongodb-scala-helper.configuration") match {
      case configFile: String =>
        stream = new FileInputStream(new File(configFile))
        if (logger.isDebugEnabled)
          logger.debug("carregado configuracoes de: %s" format (configFile))
      case _ =>
        stream = pickJarBasedOn getResourceAsStream propertyFile
        if (logger.isDebugEnabled)
          logger.debug("carregado configuracoes do pacote: %s" format (propertyFile))
    }
    if (stream ne null)
      quietlyDispose(props load stream, stream.close)
    props
  }

  protected def quietlyDispose(action: => Unit, disposal: => Unit) = {
    try {
      action
    }
    finally {
      try {
        disposal
      }
      catch {
        case e: IOException =>
          logger.error("erro loading properties " + propertyFile, e)
      }
    }
  }

  def mongodbHost = properties.get("mongodb.host").toString

  def mongodbPort = properties.get("mongodb.port").toString.toInt

  def mongodbDatabase = properties.get("mongodb.database").toString

}