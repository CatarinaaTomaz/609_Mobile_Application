package com.exemplo.atecapp // ⚠️ CONFIRMA O PACKAGE

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Tem de ter o mesmo nome do XML

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val pass = etPassword.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty()) {
                fazerLogin(email, pass)
            } else {
                Toast.makeText(this, "Preenche tudo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fazerLogin(email: String, pass: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resposta = RetrofitClient.instance.login(LoginRequest(email, pass))

                withContext(Dispatchers.Main) {
                    if (resposta.isSuccessful && resposta.body() != null) {
                        val dados = resposta.body()!!
                        Toast.makeText(this@LoginActivity, "Olá ${dados.user.nome_completo}!", Toast.LENGTH_LONG).show()

                        // Guardar token
                        val prefs = getSharedPreferences("atec_app", Context.MODE_PRIVATE)
                        prefs.edit().putString("TOKEN", dados.token).apply()

                    } else {
                        Toast.makeText(this@LoginActivity, "Login falhou!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}