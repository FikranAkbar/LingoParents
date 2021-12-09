package com.glints.lingoparents.utils

import com.auth0.android.jwt.JWT

object JWTUtils {
    fun getIdFromAccessToken(token: String): String {
        val jwt = JWT(token)
        val dataClaim = jwt.getClaim("data")
        val dataObj = dataClaim.asObject(JWTData::class.java)
        return dataObj?.id.toString()
    }

    private data class JWTData(
        val id: Int,
        val email: String,
        val role: String
    )
}