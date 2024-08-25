package com.example.ghtk_shareref.model

data class Pokemon(
    val count : Int = 0,
    val next : String = "",
    val previous : String = "",
    var results : MutableList<Result> = mutableListOf()
)
