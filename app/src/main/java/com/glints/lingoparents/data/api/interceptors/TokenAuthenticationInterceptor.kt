package com.glints.lingoparents.data.api.interceptors

import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection

class TokenAuthenticationInterceptor : Interceptor {

    private val tokenPreferences = TokenPreferences.getInstance(null)
    private var accessToken = ""
    private var refreshToken = ""


    override fun intercept(chain: Interceptor.Chain): Response {
        runBlocking {
            accessToken = tokenPreferences.getAccessToken().first()
            refreshToken = tokenPreferences.getRefreshToken().first()
        }
        val requestWithAccessToken =
            chain.request().newBuilder().addHeader("authorization", accessToken).build()
        var response = chain.proceed(requestWithAccessToken)

        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            response.close()
            val requestWithAccessRefreshToken =
                chain.request().newBuilder()
                    .addHeader("authorization", accessToken)
                    .addHeader("refreshToken", refreshToken).build()
            response = chain.proceed(requestWithAccessRefreshToken)

            runBlocking {
                val newAccessToken = response.headers["accessToken"].toString()
                tokenPreferences.saveAccessToken(newAccessToken)
            }

            response.newBuilder().body(response.body).build()
        }
        return response.newBuilder().body(response.body).build()
    }
}