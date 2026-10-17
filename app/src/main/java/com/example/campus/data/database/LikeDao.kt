package com.example.campus.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LikeDao {
    @Insert
    suspend fun insert(like: Like)

    @Query("DELETE FROM likes WHERE postId = :postId AND userId = :userId")
    suspend fun delete(postId: Long, userId: Long)

    @Query("SELECT COUNT(*) FROM likes WHERE postId = :postId")
    suspend fun getLikeCountForPost(postId: Long): Int

    @Query("SELECT * FROM likes WHERE postId = :postId AND userId = :userId")
    suspend fun getLike(postId: Long, userId: Long): Like?
}
