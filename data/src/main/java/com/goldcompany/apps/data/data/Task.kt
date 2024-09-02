package com.goldcompany.apps.data.data

data class Task(
    val id: String = System.currentTimeMillis().toString(),
    val isCompleted: Boolean,
    val title: String,
    val description: String,
    val date: String
)