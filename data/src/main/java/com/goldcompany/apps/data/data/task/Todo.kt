package com.goldcompany.apps.data.data.task

data class Todo(
    val id: String = System.currentTimeMillis().toString(),
    val isCompleted: Boolean,
    val title: String,
    val description: String,
    val dateMilli: Long
)