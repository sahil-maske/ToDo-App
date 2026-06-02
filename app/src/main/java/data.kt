package com.sahil.todoapp

data class Task (
    var id :Int = 1,
    var title : String = "",
    var description: String = "",
    var isComplete : Boolean = false
)
