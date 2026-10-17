package com.example.campus.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "likes")
data class Like(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val postId: Long,
    val userId: Long
)
