package com.example.campus

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 跳转到实际的 UI MainActivity（在 ui 包中）
        startActivity(Intent(this, com.example.campus.ui.MainActivity::class.java))
        finish()
    }
}