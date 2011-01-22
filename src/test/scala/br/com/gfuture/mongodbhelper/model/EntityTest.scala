package br.com.gfuture.mongodbhelper.model

final class EntityTest() extends Entity[EntityTest](classOf[EntityTest]) {

  var title: String = null
  var description: String = null
  var transient: String = null

  transientFields += "transient"

}