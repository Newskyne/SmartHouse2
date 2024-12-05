package com.example.smarthouse2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

class LoginActivity : AppCompatActivity() {

    private lateinit var pinDots: Array<ImageView>
    private var pinLength = 4
    private var currentPinLength = 1
    private val enteredPinCode = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        pinDots = arrayOf(
            findViewById(R.id.pinDot1),
            findViewById(R.id.pinDot2),
            findViewById(R.id.pinDot3),
            findViewById(R.id.pinDot4)
        )

        // Настройка кнопок
        val buttons = arrayOf(
            findViewById<Button>(R.id.button1),
            findViewById<Button>(R.id.button2),
            findViewById<Button>(R.id.button3),
            findViewById<Button>(R.id.button4),
            findViewById<Button>(R.id.button5),
            findViewById<Button>(R.id.button6),
            findViewById<Button>(R.id.button7),
            findViewById<Button>(R.id.button8),
            findViewById<Button>(R.id.button9),
            findViewById<Button>(R.id.button0)
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                if (currentPinLength < pinLength) {
                    val number = button.text.toString().toInt()
                    enteredPinCode.append(number)
                    updatePinIndicators()
                    currentPinLength++

                    if (currentPinLength == pinLength) {
                        checkPinCode()
                    }
                }
            }
        }
    }

    private fun updatePinIndicators() {
        for (i in 0 until currentPinLength) {
            pinDots[i].setBackgroundResource(R.drawable.filled_circle)
        }
        for (i in currentPinLength until pinLength) {
            pinDots[i].setBackgroundResource(R.drawable.empty_circle)
        }
    }

    private fun checkPinCode() {
        val sharedPreferences = getSharedPreferences("PinCodePrefs", Context.MODE_PRIVATE)
        val savedPinCode = sharedPreferences.getString("pin_code", "")

        if (enteredPinCode.toString() == savedPinCode) {
            navigateToMainActivityWithDelay()
        } else {
            Toast.makeText(this, "Неверный PIN-код", Toast.LENGTH_SHORT).show()
            resetPinIndicators()
        }
    }

    private fun resetPinIndicators() {
        currentPinLength = 0
        enteredPinCode.clear()
        updatePinIndicators()
    }

    private fun navigateToMainActivityWithDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Закрываем текущую активность, чтобы пользователь не мог вернуться назад
        }, 500) // Задержка в 500 миллисекунд (0.5 секунды)
    }
}