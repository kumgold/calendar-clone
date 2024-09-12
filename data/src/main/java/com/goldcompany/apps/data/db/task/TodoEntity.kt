package com.goldcompany.apps.data.db.task

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo val id: Long = 0,
    @ColumnInfo val isCompleted: Boolean = false,
    @ColumnInfo val title: String = "",
    @ColumnInfo val description: String = "",
    @ColumnInfo val dateTimeMilli: Long = System.currentTimeMillis()
)