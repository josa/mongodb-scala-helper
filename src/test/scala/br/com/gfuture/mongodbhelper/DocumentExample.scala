package br.com.gfuture.mongodbhelper

import annotations.DocElement

class DocumentExample  extends Document(classOf[DocumentExample]) {

    @DocElement
    var valueOne: String = null
    var valueTransient: String = null

}