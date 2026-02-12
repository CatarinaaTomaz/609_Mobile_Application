package com.example.a609_projetofinal


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // ⚠️ IMPORTANTE:
    // Se usas Emulador Android: usa "http://10.0.2.2:3000/api/"
    // Se usas Telemóvel Físico: usa o IP do teu PC (ex: "http://192.168.1.88:3000/api/")
    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}