package com.example.campus.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campus.databinding.ActivityCommentListBinding
import com.example.campus.data.database.AppDatabase
import com.example.campus.data.database.Comment
import com.example.campus.data.model.CommentItem
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.Intent

class CommentListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentListBinding
    private val scope = MainScope()
    private lateinit var adapter: CommentAdapter
    private var postId: Long = -1
    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postId = intent.getLongExtra("postId", -1)
        userId = intent.getLongExtra("userId", -1)

        adapter = CommentAdapter()
        binding.recyclerViewComments.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewComments.adapter = adapter

        val db = AppDatabase.getDatabase(this)
        val commentDao = db.commentDao()

        scope.launch(Dispatchers.IO) {
            val userDao = db.userDao()
            commentDao.getCommentsForPost(postId).collectLatest { list ->
                val mapped = list.map { c ->
                    val u = userDao.getUserById(c.userId)
                    CommentItem(
                        id = c.id,
                        username = u?.username ?: "用户${c.userId}",
                        content = c.content,
                        timestamp = c.timestamp
                    )
                }
                withContext(Dispatchers.Main) {
                    adapter.submitList(mapped)
                }
            }
        }

        binding.btnSend.setOnClickListener {
            val content = binding.etComment.text.toString().trim()
            if (content.isNotEmpty()) {
                scope.launch(Dispatchers.IO) {
                    db.commentDao().insert(
                        Comment(
                            postId = postId,
                            userId = userId,
                            content = content,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    withContext(Dispatchers.Main) {
                        binding.etComment.setText("")
                        // Notify feed to increment comment count immediately
                        val intent = Intent("COMMENT_ADDED")
                        intent.putExtra("postId", postId)
                        LocalBroadcastManager.getInstance(this@CommentListActivity).sendBroadcast(intent)
                    }
                }
            }
        }
    }
}
