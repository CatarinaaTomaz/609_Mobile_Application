package com.example.a609_projetofinal

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
// IMPORTANTE: Applandeo Calendar imports
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private var userId: Int = 0
    private var token: String = ""
    private var listaCompletaAulas: List<HorarioAula> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 1. Recuperar dados
        val prefs = getSharedPreferences("atec_app", MODE_PRIVATE)
        val nome = prefs.getString("NOME_USER", "Utilizador")
        val roleString = prefs.getString("ROLE_USER", "Visitante")
        token = prefs.getString("TOKEN", "") ?: ""
        userId = prefs.getInt("USER_ID", 0)

        if (token.isEmpty() || userId == 0) {
            Toast.makeText(this, "Sessão inválida.", Toast.LENGTH_SHORT).show()
            logout()
            return
        }

        // 2. UI
        val tvNome = findViewById<TextView>(R.id.tvNomeUser)
        val tvRole = findViewById<TextView>(R.id.tvRoleUser)
        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        tvNome.text = nome
        tvRole.text = getRoleName(roleString)

        // 3. Listener do Calendário (Ao clicar num dia)
        calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val calendar = eventDay.calendar
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dataSelecionada = sdf.format(calendar.time)

                Log.d("CALENDARIO", "Clicaste em: $dataSelecionada")
                filtrarAulasPorData(dataSelecionada)
            }
        })

        btnLogout.setOnClickListener { logout() }

        // 4. Buscar Dados
        carregarDashboard()
    }

    private fun carregarDashboard() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tokenHeader = "Bearer $token"

                // A. Buscar Cursos
                var listaCursos: List<Curso>? = null
                try {
                    val res = RetrofitClient.instance.getMeusCursos(userId, tokenHeader)
                    if (res.isSuccessful) listaCursos = res.body()
                } catch (e: Exception) { Log.e("API_CURSOS", "Erro: ${e.message}") }

                // B. Buscar Horário
                val resHorario = RetrofitClient.instance.getMeuHorario(userId, tokenHeader)

                withContext(Dispatchers.Main) {
                    // UI Cursos
                    if (listaCursos != null) preencherListaCursos(listaCursos)
                    else mostrarErro(findViewById(R.id.containerCursos), "Cursos indisponíveis.")

                    // UI Horário
                    if (resHorario.isSuccessful && resHorario.body() != null) {
                        listaCompletaAulas = resHorario.body()!!

                        Log.d("API_AULAS", "Recebi ${listaCompletaAulas.size} aulas do servidor.")

                        // 1. PINTAR AS BOLINHAS (Chama a função corrigida)
                        marcarDiasNoCalendario(listaCompletaAulas)

                        // 2. Selecionar o dia de hoje na lista
                        val hoje = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date())
                        filtrarAulasPorData(hoje)
                    } else {
                        mostrarErro(findViewById(R.id.containerHorario), "Sem aulas carregadas.")
                    }
                }

            } catch (e: Exception) {
                Log.e("API_HOME", "Erro Geral: ${e.message}")
            }
        }
    }

    // --- FUNÇÃO CORRIGIDA E SIMPLIFICADA PARA AS BOLINHAS ---
    private fun marcarDiasNoCalendario(aulas: List<HorarioAula>) {
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val events = ArrayList<EventDay>()
        val formatDateOnly = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (aula in aulas) {
            try {
                if (aula.data_aula.length >= 10) {
                    val dataString = aula.data_aula.substring(0, 10)
                    val date = formatDateOnly.parse(dataString)

                    if (date != null) {
                        val calendar = Calendar.getInstance()
                        calendar.time = date

                        // Limpeza de horas (Fundamental!)
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)

                        // CORREÇÃO: Passar o ID DIRETO (R.drawable.ponto_azul)
                        // A biblioteca sabe redimensionar isto automaticamente.
                        // Se não tiveres o ponto_azul, usa R.drawable.ic_launcher_foreground para testar
                        events.add(EventDay(calendar, R.drawable.ponto_azul))

                        Log.d("CALENDARIO_PIN", "Bolinha ID adicionada: $dataString")
                    }
                }
            } catch (e: Exception) {
                Log.e("CALENDARIO_ERRO", "Erro: ${e.message}")
            }
        }

        calendarView.setEvents(events)
    }

    // --- FILTROS E UI ---

    private fun filtrarAulasPorData(dataStr: String) {
        val aulasDoDia = listaCompletaAulas.filter { aula ->
            aula.data_aula != null && aula.data_aula.startsWith(dataStr)
        }
        mostrarAulasNaUI(aulasDoDia)
    }

    private fun mostrarAulasNaUI(lista: List<HorarioAula>) {
        val container = findViewById<LinearLayout>(R.id.containerHorario)
        container.removeAllViews()

        if (lista.isEmpty()) {
            mostrarErro(container, "Nenhuma aula para este dia.")
            return
        }

        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM (EEEE)", Locale("pt", "PT"))

        for (aula in lista) {
            try {
                val dataRaw = aula.data_aula.substring(0, 10)
                val dataObj = inputFormat.parse(dataRaw)
                val dataBonita = outputFormat.format(dataObj!!)

                val horaInicio = if(aula.hora_inicio.length >= 5) aula.hora_inicio.substring(0, 5) else aula.hora_inicio
                val horaFim = if(aula.hora_fim.length >= 5) aula.hora_fim.substring(0, 5) else aula.hora_fim

                val tv = TextView(this)
                tv.text = "⏰ $horaInicio - $horaFim\n📘 ${aula.Modulo?.nome ?: "Módulo"}\n📍 Sala: ${aula.Sala?.nome ?: "N/A"}\n📅 $dataBonita"
                tv.setTextColor(Color.BLACK)
                tv.textSize = 16f
                tv.setPadding(0, 0, 0, 20)

                container.addView(tv)

                val linha = View(this)
                linha.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2)
                linha.setBackgroundColor(Color.LTGRAY)
                container.addView(linha)

                val spacer = View(this)
                spacer.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 30)
                container.addView(spacer)

            } catch (e: Exception) {
                Log.e("RENDER_AULA", "Erro ao desenhar aula: ${e.message}")
            }
        }
    }

    private fun preencherListaCursos(lista: List<Curso>?) {
        val container = findViewById<LinearLayout>(R.id.containerCursos)
        container.removeAllViews()
        if (lista.isNullOrEmpty()) {
            mostrarErro(container, "Sem cursos.")
            return
        }
        for (curso in lista) {
            val tv = TextView(this)
            tv.text = "🎓 ${curso.nome}"
            tv.textSize = 16f
            tv.setTextColor(Color.BLACK)
            tv.setTypeface(null, android.graphics.Typeface.BOLD)
            container.addView(tv)
        }
    }

    private fun getRoleName(roleRaw: Any?): String {
        return when(roleRaw.toString()) {
            "1", "1.0", "Admin" -> "Administrador"
            "2", "2.0", "Formando" -> "Formando"
            "3", "3.0", "Formador" -> "Formador"
            "4", "4.0", "Secretaria" -> "Secretaria"
            else -> roleRaw.toString().replaceFirstChar { it.uppercase() }
        }
    }

    private fun mostrarErro(container: LinearLayout, msg: String) {
        container.removeAllViews()
        val tv = TextView(this)
        tv.text = msg
        tv.setTextColor(Color.GRAY)
        tv.setPadding(10, 10, 10, 10)
        container.addView(tv)
    }

    private fun logout() {
        getSharedPreferences("atec_app", Context.MODE_PRIVATE).edit().clear().apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}