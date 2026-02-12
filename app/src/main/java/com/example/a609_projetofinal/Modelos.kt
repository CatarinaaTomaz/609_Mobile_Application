package com.example.a609_projetofinal

import com.google.gson.annotations.SerializedName

// Dados que enviamos para verificar o código
data class VerifyRequest(
    val userId: Int,
    val codigo: String
)

// A Resposta que o servidor envia (Aqui estavam a faltar os campos!)
data class VerifyResponse(
    val token: String,
    val user: UserData
)

// Os detalhes do utilizador
data class UserData(
    val id: Int, // ou id_user

    // A MÁGICA ESTÁ AQUI:
    // Dizemos ao Android: "Procura por 'nome'. Se não houver, tenta 'nome_completo'"
    @SerializedName(value = "nome", alternate = ["nome_completo"])
    val nome_completo: String?,

    val email: String?,

    @SerializedName(value = "role", alternate = ["roleId", "role_id"])
    val role: Any? // Usamos Any para não dar erro de tipo
)

data class LoginResponse(
    val msg: String?,
    val userId: Int?,       // Pode vir null se for login direto
    val require2fa: Boolean,
    val token: String?,     // NOVO: Recebe o token se não houver 2FA
    val user: UserData?     // NOVO: Recebe os dados do user se não houver 2FA
)

// Dados para reenviar o código (Login)
data class LoginRequest(
    val email: String,
    val password: String
)

// --- NOVOS MODELOS PARA A HOME ---

// Para a lista de Cursos
data class Curso(
    val id_curso: Int,
    val nome: String,
    val area: String?
    // Adiciona outros campos se o teu backend enviar (ex: data_inicio)
)

// Para a lista de Horários (Aulas)
// Ajusta os nomes dos campos exatamente como o teu backend envia!
data class HorarioAula(
    val id_horario: Int,
    val data_aula: String,   // Ex: "2024-02-20T00:00:00.000Z"
    val hora_inicio: String, // Ex: "09:00:00"
    val hora_fim: String,
    val Modulo: ModuloInfo?, // O backend envia objetos aninhados
    val Sala: SalaInfo?
)

// Informação extra dentro do horário
data class ModuloInfo(val nome: String)
data class SalaInfo(val nome: String)