package com.example.campus.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.campus.databinding.ItemCommentBinding
import com.example.campus.data.model.CommentItem

class CommentAdapter : ListAdapter<CommentItem, CommentAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CommentItem>() {
            override fun areItemsTheSame(oldItem: CommentItem, newItem: CommentItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CommentItem, newItem: CommentItem): Boolean =
                oldItem == newItem
        }
    }

    inner class VH(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvUsername.text = item.username
            tvContent.text = item.content
            tvTime.text = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", item.timestamp)
        }
    }
}
