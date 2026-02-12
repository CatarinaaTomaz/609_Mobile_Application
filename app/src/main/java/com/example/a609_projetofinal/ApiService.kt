package com.example.a609_projetofinal

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Rota de Verificar 2FA
    @POST("auth/verify-2fa")
    suspend fun verificarCode(@Body request: VerifyRequest): Response<VerifyResponse>

    // Rota de Login
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // --- NOVAS ROTAS ---

    // 1. Buscar Cursos
    // Nota: Se esta rota não existir no backend, vai dar erro 404, mas a app não crasha.
    @GET("users/{id}/cursos")
    suspend fun getMeusCursos(
        @Path("id") userId: Int,
        @Header("Authorization") token: String
    ): Response<List<Curso>>

    // 2. Buscar Horário
    // O @Query("userId") transforma isto em: .../api/horarios?userId=3
    @GET("horarios")
    suspend fun getMeuHorario(
        @Query("userId") userId: Int,
        @Header("Authorization") token: String
    ): Response<List<HorarioAula>>
}