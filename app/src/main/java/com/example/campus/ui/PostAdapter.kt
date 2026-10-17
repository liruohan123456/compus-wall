package com.example.campus.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.campus.databinding.ItemPostBinding
import com.example.campus.data.model.PostFeedItem

class PostAdapter(
    private val currentUserId: Long,
    private val onLike: (PostFeedItem) -> Unit,
    private val onDelete: (PostFeedItem) -> Unit,
    private val onComment: (PostFeedItem) -> Unit
) : ListAdapter<PostFeedItem, PostAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<PostFeedItem>() {
            override fun areItemsTheSame(oldItem: PostFeedItem, newItem: PostFeedItem): Boolean =
                oldItem.postId == newItem.postId

            override fun areContentsTheSame(oldItem: PostFeedItem, newItem: PostFeedItem): Boolean =
                oldItem == newItem
        }
    }

    inner class VH(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvUsername.text = item.username
            tvContent.text = item.content
            tvCounts.text = "点赞 ${item.likeCount} · 评论 ${item.commentCount}"
            btnLike.text = if (item.isLiked) "取消点赞" else "点赞"
            btnLike.setOnClickListener { onLike(item) }
            btnDelete.setOnClickListener { onDelete(item) }
            btnDelete.visibility = if (item.userId == currentUserId) android.view.View.VISIBLE else android.view.View.GONE
            btnComment.setOnClickListener { onComment(item) }
        }
    }
}
