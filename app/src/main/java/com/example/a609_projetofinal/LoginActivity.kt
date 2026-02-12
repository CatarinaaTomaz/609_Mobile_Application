package com.example.a609_projetofinal

import android.content.Context
import android.content.Intent // Importante para mudar de ecrã
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
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // (Opcional) Verificar se já existe login feito para saltar direto
        // verificarSessaoExistente()

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

                        if (dados.require2fa) {
                            // === CASO 1: PRECISA DE CÓDIGO (ADMIN / COM 2FA) ===
                            Toast.makeText(this@LoginActivity, "Código enviado para o email!", Toast.LENGTH_LONG).show()

                            val intent = Intent(this@LoginActivity, TwoFactorActivity::class.java)
                            // Passamos o userId (garante que o backend enviou, usa 0 se for null)
                            intent.putExtra("USER_ID", dados.userId ?: 0)
                            // Passamos email/pass para o caso de precisar de reenviar
                            intent.putExtra("USER_EMAIL", email)
                            intent.putExtra("USER_PASS", pass)
                            startActivity(intent)

                        } else {
                            // === CASO 2: LOGIN DIRETO (SEM 2FA) ===
                            // O backend já mandou o token e o user!
                            if (dados.token != null && dados.user != null) {
                                val prefs = getSharedPreferences("atec_app", Context.MODE_PRIVATE)
                                with(prefs.edit()) {
                                    putString("TOKEN", dados.token)
                                    putString("NOME_USER", dados.user.nome_completo)
                                    putString("ROLE_USER", dados.user.role.toString())
                                    putInt("USER_ID", dados.userId ?: dados.user?.id ?: 0)
                                    apply()
                                }

                                Toast.makeText(this@LoginActivity, "Bem-vindo, ${dados.user.nome_completo}!", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                startActivity(intent)
                                finish() // Fecha o login
                            } else {
                                Toast.makeText(this@LoginActivity, "Erro: Dados de login inválidos.", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } else {
                        // Erro de Login (Email/Pass errados)
                        Toast.makeText(this@LoginActivity, "Email ou password incorretos.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }}