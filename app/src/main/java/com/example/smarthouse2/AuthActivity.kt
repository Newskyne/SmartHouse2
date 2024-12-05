package com.example.smarthouse2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private val supabase = createSupabaseClient(
        supabaseUrl = "https://btcqpdxbworysjaawhtk.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJ0Y3FwZHhid29yeXNqYWF3aHRrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzMyMTM4NjEsImV4cCI6MjA0ODc4OTg2MX0.NQHlGSpxAVoirXd1P7HPYGEEKLaNShnJGCgEkZuH0IA"
    ) {
        install(Auth)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    supabase.auth.signInWith(Email, password) {
                        this.email = email
                        this.password = password
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AuthActivity, "Вход успешен", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        handleLoginError(e)
                    }
                }
            }
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun handleLoginError(e: Exception) {
        when {
            e.message?.contains("Invalid login credentials") == true -> {
                Toast.makeText(this, "Неверные учетные данные", Toast.LENGTH_SHORT).show()
            }
            e.message?.contains("email rate limit exceeded") == true -> {
                Toast.makeText(this, "Превышен лимит на количество запросов", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Ошибка входа: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}