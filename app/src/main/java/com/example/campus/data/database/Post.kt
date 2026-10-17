package com.example.campus.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val content: String,
    val timestamp: Long
)
