package com.glints.lingoparents.data.api

import android.content.Context
import com.glints.lingoparents.utils.TokenPreferences
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.HTTP
import java.util.concurrent.TimeUnit

object APIClient {
    private const val BASE_URL = "http://192.168.1.8:3000"

    private val gson = GsonBuilder().setLenient().create()

    private val httpLoggingInterceptor = HttpLoggingInterceptor().also {
        it.level = HttpLoggingInterceptor.Level.BODY
    }

    private val tokenAuthenticationInterceptor = Interceptor { chain ->
        val requestWithAccessToken =
            chain.request().newBuilder().addHeader("authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjp7ImlkIjozMywiZW1haWwiOiJjYWx2aW5zYW4xMjNAZ21haWwuY29tIiwicm9sZSI6ImFkbWluIn0sImlhdCI6MTYzODQzNDQzNCwiZXhwIjoxNjM4NDM0NDU0fQ._KuXj5HaQ2kobfidzy64nOU5fYgLFTaoIKMCBRjuAMk").build()
        var response = chain.proceed(requestWithAccessToken)

        if (response.code == 401) {
            response.close()
            val requestWithAccessRefreshToken =
                chain.request().newBuilder()
                    .addHeader("authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjp7ImlkIjozMywiZW1haWwiOiJjYWx2aW5zYW4xMjNAZ21haWwuY29tIiwicm9sZSI6ImFkbWluIn0sImlhdCI6MTYzODQzNDQzNCwiZXhwIjoxNjM4NDM0NDU0fQ._KuXj5HaQ2kobfidzy64nOU5fYgLFTaoIKMCBRjuAMk")
                    .addHeader("refreshToken", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjp7ImlkIjozMywiZW1haWwiOiJjYWx2aW5zYW4xMjNAZ21haWwuY29tIiwicm9sZSI6ImFkbWluIn0sImlhdCI6MTYzODQzNDQzNCwiZXhwIjoxNjM4NTIwODM0fQ.TAERFuNEqE6nRlkdUGgXP_xWdDN-TSGniIZKPlMR3kk").build()
            response = chain.proceed(requestWithAccessRefreshToken)

            // new generated access token
            val newAccessToken = response.headers["accessToken"].toString()

            response.newBuilder().body(response.body).build()
        }
        response.newBuilder().body(response.body).build()
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(tokenAuthenticationInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()


    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun retrofit(): Retrofit {
        return retrofit
    }

    val service: APIService
        get() = retrofit().create(APIService::class.java)
}