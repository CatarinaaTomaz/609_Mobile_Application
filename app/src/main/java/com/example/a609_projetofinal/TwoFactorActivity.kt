package com.example.a609_projetofinal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class TwoFactorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_factor)

        // Receber dados da Activity anterior
        val userId = intent.getIntExtra("USER_ID", 0)
        val email = intent.getStringExtra("USER_EMAIL") ?: ""
        val pass = intent.getStringExtra("USER_PASS") ?: ""

        val etCodigo = findViewById<EditText>(R.id.etCodigo2FA)
        val btnVerificar = findViewById<Button>(R.id.btnVerificar)
        val tvReenviar = findViewById<TextView>(R.id.tvReenviar)

        btnVerificar.setOnClickListener {
            val codigo = etCodigo.text.toString().trim()

            if (codigo.length == 6) {
                validarCodigo(userId, codigo)
            } else {
                Toast.makeText(this, "O código deve ter 6 dígitos", Toast.LENGTH_SHORT).show()
            }
        }

        tvReenviar.setOnClickListener {
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                reenviarCodigo(email, pass)
            } else {
                Toast.makeText(this, "Dados insuficientes para reenviar.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validarCodigo(userId: Int, codigo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Log para debug
                Log.d("2FA", "Enviando: UserID=$userId, Codigo=$codigo")

                val request = VerifyRequest(userId, codigo)
                val resposta = RetrofitClient.instance.verificarCode(request)

                withContext(Dispatchers.Main) {
                    if (resposta.isSuccessful && resposta.body() != null) {
                        val dados = resposta.body()!!

                        // Guardar Token e Dados
                        val prefs = getSharedPreferences("atec_app", Context.MODE_PRIVATE)
                        with(prefs.edit()) {
                            putString("TOKEN", dados.token)
                            putString("NOME_USER", dados.user.nome_completo)
                            putString("ROLE_USER", dados.user.role.toString())
                            putInt("USER_ID", dados.user.id)
                            apply()
                        }

                        Toast.makeText(this@TwoFactorActivity, "Login com Sucesso!", Toast.LENGTH_SHORT).show()

                        // Ir para a Home
                        val intent = Intent(this@TwoFactorActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finishAffinity() // Fecha todas as janelas anteriores (Login)

                    } else {
                        // Tenta ler a mensagem de erro do servidor
                        val errorMsg = try {
                            JSONObject(resposta.errorBody()?.string()).getString("msg")
                        } catch (e: Exception) {
                            "Código incorreto ou expirado."
                        }
                        Toast.makeText(this@TwoFactorActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("2FA", "Erro API: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TwoFactorActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun reenviarCodigo(email: String, pass: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resposta = RetrofitClient.instance.login(LoginRequest(email, pass))
                withContext(Dispatchers.Main) {
                    if (resposta.isSuccessful) {
                        Toast.makeText(this@TwoFactorActivity, "Novo código enviado para o email!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@TwoFactorActivity, "Erro ao reenviar código.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TwoFactorActivity, "Erro de rede.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}