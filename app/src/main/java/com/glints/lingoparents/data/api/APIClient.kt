package com.glints.lingoparents.data.api

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object APIClient {
    private const val BASE_URL = "http://be-server.ipe-glintsacademy.com:3000"
    //private const val BASE_URL = "http://192.168.43.167:3000"

    private val gson = GsonBuilder().setLenient().create()

    private val httpLoggingInterceptor = HttpLoggingInterceptor().also {
        it.level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(Interceptor { chain ->
            val request: Request = chain.request()
            chain.proceed(request)
        })
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