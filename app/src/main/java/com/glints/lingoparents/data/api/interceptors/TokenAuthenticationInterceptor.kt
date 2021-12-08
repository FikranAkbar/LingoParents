package com.glints.lingoparents.data.api.interceptors

import android.util.Log
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.net.HttpURLConnection

class TokenAuthenticationInterceptor : Interceptor {
    private val tokenPreferences = TokenPreferences.getInstance(null)

    private var accessToken = ""
    private var refreshToken = ""

    override fun intercept(chain: Interceptor.Chain): Response {
        runBlocking {
            if (accessToken.isBlank()) {
                accessToken = tokenPreferences.getAccessToken().first()
                Log.d("AccessToken", accessToken)
            }
            if (refreshToken.isBlank()) {
                refreshToken = tokenPreferences.getRefreshToken().first()
                Log.d("RefreshToken", refreshToken)
            }
        }
        val requestWithAccessToken =
            chain.request().newBuilder().addHeader("authorization", accessToken).build()
        var response = chain.proceed(requestWithAccessToken)

        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            var jObjError = JSONObject(response.peekBody(2048).string())
            var message = jObjError["message"] as String

            if (message.contains("'refreshToken' not found", true)) {
                val requestWithAccessRefreshToken =
                    chain.request().newBuilder()
                        .addHeader("authorization", accessToken)
                        .addHeader("refreshToken", refreshToken).build()
                response = chain.proceed(requestWithAccessRefreshToken)
            }

            if (response.isSuccessful) {
                val newAccessToken = response.headers["accessToken"]
                if (newAccessToken != null) {
                    Log.d("NewAccessToken", newAccessToken)
                    accessToken = newAccessToken
                    runBlocking {
                        tokenPreferences.saveAccessToken(newAccessToken)
                    }
                }
            } else {
                jObjError = JSONObject(response.peekBody(2048).string())
                message = jObjError["message"] as String
            }

            if (message.contains("Refreshing access token failed", true)) {
                EventBus.getDefault()
                    .post(TokenAuthenticationEvent.RefreshTokenExpiredEvent)
            }
        }

        return response.newBuilder().body(response.body).build()
    }

    sealed class TokenAuthenticationEvent {
        object RefreshTokenExpiredEvent : TokenAuthenticationEvent()
    }
}