package br.com.gfuture.mongodbhelper.log

import org.slf4j.LoggerFactory

/**
 * Abstrai o log da aplicação
 *
 * User: Jeosadache Galvão
 */
trait Logged {

  protected lazy val logger = LoggerFactory.getLogger(getClass)

}