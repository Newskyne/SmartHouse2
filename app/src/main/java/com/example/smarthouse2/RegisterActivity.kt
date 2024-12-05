package com.example.smarthouse2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var usernameErrorTextView: TextView
    private lateinit var emailErrorTextView: TextView
    private lateinit var passwordErrorTextView: TextView

    private val supabase = createSupabaseClient(
        supabaseUrl = "https://btcqpdxbworysjaawhtk.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJ0Y3FwZHhid29yeXNqYWF3aHRrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzMyMTM4NjEsImV4cCI6MjA0ODc4OTg2MX0.NQHlGSpxAVoirXd1P7HPYGEEKLaNShnJGCgEkZuH0IA"
    ) {
        install(Auth)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)
        loginButton = findViewById(R.id.loginButton)
        usernameErrorTextView = findViewById(R.id.usernameErrorTextView)
        emailErrorTextView = findViewById(R.id.emailErrorTextView)
        passwordErrorTextView = findViewById(R.id.passwordErrorTextView)

        setupTextWatchers()

        registerButton.setOnClickListener {
            if (validateFields()) {
                val username = usernameEditText.text.toString().trim()
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        supabase.auth.signUpWith(Email,password) {
                            this.email = email
                            this.password = password
                        }

                        withContext(Dispatchers.Main) {
                            val sharedPreferences = getSharedPreferences("SmartHomePrefs", MODE_PRIVATE)
                            sharedPreferences.edit().putBoolean("isRegistered", true).apply()

                            Toast.makeText(this@RegisterActivity, "Регистрация успешна", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, CreatePinActivity::class.java))
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegisterActivity, "Ошибка регистрации: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Ошибка валидации", Toast.LENGTH_SHORT).show()
            }
        }

        loginButton.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }
    }

    private fun setupTextWatchers() {
        usernameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateUsername(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateFields(): Boolean {
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        var isValid = true

        if (!validateUsername(username)) {
            isValid = false
        }

        if (!validateEmail(email)) {
            isValid = false
        }

        if (!validatePassword(password)) {
            isValid = false
        }

        return isValid
    }

    private fun validateUsername(username: String): Boolean {
        return if (username.length > 10 || !username.matches(Regex("[a-zA-Z0-9]+"))) {
            usernameErrorTextView.text = "Имя пользователя должно быть не более 10 символов и содержать только буквы и цифры"
            usernameErrorTextView.setTextColor(resources.getColor(android.R.color.holo_red_light))
            false
        } else {
            usernameErrorTextView.text = ""
            true
        }
    }

    private fun validateEmail(email: String): Boolean {
        return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailErrorTextView.text = "Неверный формат электронной почты"
            emailErrorTextView.setTextColor(resources.getColor(android.R.color.holo_red_light))
            false
        } else {
            emailErrorTextView.text = ""
            true
        }
    }

    private fun validatePassword(password: String): Boolean {
        return if (password.length < 6 || !password.matches(Regex("[0-9]+"))) {
            passwordErrorTextView.text = "Пароль должен содержать не менее 6 цифр"
            passwordErrorTextView.setTextColor(resources.getColor(android.R.color.holo_red_light))
            false
        } else {
            passwordErrorTextView.text = ""
            true
        }
    }
}