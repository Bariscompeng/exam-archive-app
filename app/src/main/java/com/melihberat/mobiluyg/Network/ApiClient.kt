package com.melihberat.mobiluyg.Network

import android.content.Context
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Emülatörden Node backend'e
    private const val BASE_URL = "http://10.0.2.2:5000/api/"

    private fun authInterceptor(context: Context) = Interceptor { chain ->
        val original: Request = chain.request()
        val token = runBlocking { TokenStore.getToken(context) }
        val builder = original.newBuilder()
        if (!token.isNullOrBlank()) {
            builder.addHeader("Authorization", "Bearer $token")
        }
        chain.proceed(builder.build())
    }

    fun api(context: Context): ApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor(context))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}

