package com.example.campus.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.campus.data.model.PostFeedItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Insert
    suspend fun insert(post: Post)

    @Query("DELETE FROM posts WHERE id = :postId AND userId = :userId")
    suspend fun deletePost(postId: Long, userId: Long)

    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    suspend fun getPostById(postId: Long): Post?
}
