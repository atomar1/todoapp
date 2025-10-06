package com.example.todoapp


data class ToDoItem(
    val id: Long,
    val label: String,
    val isCompleted: Boolean = false
)
