package com.glints.lingoparents.data.api

import android.content.Context
import com.glints.lingoparents.data.api.interceptors.TokenAuthenticationInterceptor
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
    private const val BASE_URL = "http://178.128.30.32/"

    private val gson = GsonBuilder().setLenient().create()

    private val httpLoggingInterceptor = HttpLoggingInterceptor().also {
        it.level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(TokenAuthenticationInterceptor())
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