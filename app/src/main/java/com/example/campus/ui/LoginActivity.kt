package com.example.campus.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.campus.databinding.ActivityLoginBinding
import com.example.campus.data.database.AppDatabase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()
            if (username.isEmpty() || password.isEmpty()) {
                binding.tvError.text = "请输入用户名和密码"
                return@setOnClickListener
            }
            scope.launch(Dispatchers.IO) {
                val user = userDao.getUserByUsername(username)
                withContext(Dispatchers.Main) {
                    if (user == null || user.passwordHash != password) {
                        binding.tvError.text = "用户名或密码错误"
                    } else {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                            putExtra("userId", user.id)
                            putExtra("username", user.username)
                        })
                        finish()
                    }
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
