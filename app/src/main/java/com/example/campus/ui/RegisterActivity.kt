package com.example.campus.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.campus.databinding.ActivityRegisterBinding
import com.example.campus.data.database.AppDatabase
import com.example.campus.data.database.User
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()
            if (username.isEmpty() || password.isEmpty()) {
                binding.tvError.text = "请输入用户名和密码"
                return@setOnClickListener
            }
            scope.launch(Dispatchers.IO) {
                val existing = userDao.getUserByUsername(username)
                if (existing != null) {
                    withContext(Dispatchers.Main) {
                        binding.tvError.text = "用户名已存在"
                    }
                } else {
                    userDao.insert(User(username = username, passwordHash = password))
                    withContext(Dispatchers.Main) {
                        finish()
                    }
                }
            }
        }
    }
}
