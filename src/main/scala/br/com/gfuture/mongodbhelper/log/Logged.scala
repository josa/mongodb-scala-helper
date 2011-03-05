package br.com.gfuture.mongodbhelper.log

import org.slf4j.LoggerFactory

/**
 * Abstrai o log da aplicação
 *
 * User: Jeosadache Galvão
 */
trait Logged {

  protected lazy val logger = LoggerFactory.getLogger(getClass)

  def error(throwable: Throwable) = logger.error(throwable.getMessage, throwable)

  def error(message: Unit, throwable: Throwable) = logger.error(message.toString, throwable)

  def info(message: String) = logger.info(message)

  def debug(message: Unit) = {
    if (logger.isDebugEnabled)
      logger.debug(message.toString)
  }

}