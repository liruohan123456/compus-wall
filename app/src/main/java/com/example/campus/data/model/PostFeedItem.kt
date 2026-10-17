package com.example.campus.data.model

data class PostFeedItem(
    val postId: Long,
    val userId: Long,
    val username: String,
    val content: String,
    val timestamp: Long,
    val likeCount: Int,
    val commentCount: Int,
    val isLiked: Boolean
)
