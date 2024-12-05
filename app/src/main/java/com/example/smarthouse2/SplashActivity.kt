package com.example.smarthouse2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthouse2.RegisterActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoImageView = findViewById<ImageView>(R.id.logoImageView)
        logoImageView.alpha = 0f
        logoImageView.animate().setDuration(1500).alpha(1f).withEndAction {
            // Добавляем задержку в 2 секунды перед переходом к MainActivity
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, RegisterActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }, 2000) // 2 секунды
        }.start()
    }
}