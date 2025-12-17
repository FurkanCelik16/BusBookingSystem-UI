package com.example.busbookingsystem.data.api

import com.example.busbookingsystem.utils.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5281/"
    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        val original = chain.request()
        val token = TokenManager.getToken()

        val requestBuilder = original.newBuilder()

        if (token != null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        chain.proceed(request)
    }.build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
    val authService: ApiService get() = api
}