package com.example.campus.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val postId: Long,
    val userId: Long,
    val content: String,
    val timestamp: Long
)
