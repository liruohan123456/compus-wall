package com.example.campus.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Insert
    suspend fun insert(comment: Comment)

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Long): Flow<List<Comment>>

    @Query("SELECT COUNT(*) FROM comments WHERE postId = :postId")
    suspend fun getCommentCountForPost(postId: Long): Int
}
