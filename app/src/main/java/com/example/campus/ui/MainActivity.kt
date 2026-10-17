package com.example.campus.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campus.databinding.ActivityMainBinding
import com.example.campus.data.database.AppDatabase
import com.example.campus.data.database.Post
import com.example.campus.data.database.Like
import com.example.campus.data.model.PostFeedItem
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.campus.data.database.Comment
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val scope = MainScope()
    private lateinit var adapter: PostAdapter
    private var userId: Long = -1
    private var username: String = ""
    private val commentAddedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: android.content.Intent?) {
            val pid = intent?.getLongExtra("postId", -1L) ?: -1L
            if (pid > 0) {
                val current = adapter.currentList.toMutableList()
                val idx = current.indexOfFirst { it.postId == pid }
                if (idx >= 0) {
                    val item = current[idx]
                    current[idx] = item.copy(commentCount = item.commentCount + 1)
                    adapter.submitList(current)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getLongExtra("userId", -1)
        username = intent.getStringExtra("username") ?: ""

        adapter = PostAdapter(
            currentUserId = userId,
            onLike = { item -> toggleLikeOptimistic(item) },
            onDelete = { item -> deletePost(item) },
            onComment = { item -> openComments(item) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        val db = AppDatabase.getDatabase(this)
        val postDao = db.postDao()

        scope.launch(Dispatchers.IO) {
            postDao.getAllPosts().collectLatest { posts ->
                val userDao = db.userDao()
                val likeDao = db.likeDao()
                val commentDao = db.commentDao()
                val items = posts.map { p ->
                    val u = userDao.getUserById(p.userId)
                    val likeCount = likeDao.getLikeCountForPost(p.id)
                    val commentCount = commentDao.getCommentCountForPost(p.id)
                    val isLiked = likeDao.getLike(p.id, userId) != null
                    PostFeedItem(
                        postId = p.id,
                        userId = p.userId,
                        username = u?.username ?: "用户${p.userId}",
                        content = p.content,
                        timestamp = p.timestamp,
                        likeCount = likeCount,
                        commentCount = commentCount,
                        isLiked = isLiked
                    )
                }
                withContext(Dispatchers.Main) {
                    adapter.submitList(items)
                }
            }
        }

        binding.btnPost.setOnClickListener {
            val content = binding.etPost.text.toString().trim()
            if (content.isNotEmpty()) {
                scope.launch(Dispatchers.IO) {
                    db.postDao().insert(Post(userId = userId, content = content, timestamp = System.currentTimeMillis()))
                    withContext(Dispatchers.Main) {
                        binding.etPost.setText("")
                        Toast.makeText(this@MainActivity, "发布成功", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(commentAddedReceiver, IntentFilter("COMMENT_ADDED"))
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(commentAddedReceiver)
        super.onStop()
    }

    private fun toggleLikeOptimistic(item: PostFeedItem) {
        val db = AppDatabase.getDatabase(this)
        // Optimistic UI update
        val currentList = adapter.currentList.toMutableList()
        val idx = currentList.indexOfFirst { it.postId == item.postId }
        if (idx >= 0) {
            val updated = item.copy(
                isLiked = !item.isLiked,
                likeCount = if (item.isLiked) item.likeCount - 1 else item.likeCount + 1
            )
            currentList[idx] = updated
            adapter.submitList(currentList)
        }
        scope.launch(Dispatchers.IO) {
            val likeDao = db.likeDao()
            if (item.isLiked) {
                likeDao.delete(item.postId, userId)
            } else {
                likeDao.insert(Like(postId = item.postId, userId = userId))
            }
        }
    }

    private fun deletePost(item: PostFeedItem) {
        val db = AppDatabase.getDatabase(this)
        scope.launch(Dispatchers.IO) {
            db.postDao().deletePost(item.postId, userId)
        }
    }

    private fun openComments(item: PostFeedItem) {
        val intent = android.content.Intent(this, CommentListActivity::class.java)
        intent.putExtra("postId", item.postId)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }
}
