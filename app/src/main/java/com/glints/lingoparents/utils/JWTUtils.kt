package com.glints.lingoparents.utils

import com.auth0.android.jwt.JWT

object JWTUtils {
    fun getIdFromAccessToken(token: String): String {
        val jwt = JWT(token)
        val dataClaim = jwt.getClaim("data")
        val dataObj = dataClaim.asObject(AccessTokenJWTData::class.java)
        return dataObj?.id.toString()
    }

    fun getDataFromGoogleIdToken(idToken: String): GoogleIdTokenData{
        val jwt = JWT(idToken)
        val email = jwt.getClaim("email").asString().toString()
        val givenName = jwt.getClaim("given_name").asString().toString()
        val familyName = jwt.getClaim("family_name").asString().toString()
        return GoogleIdTokenData(email, givenName, familyName)
    }

    private data class AccessTokenJWTData(
        val id: Int,
        val email: String,
        val role: String
    )

    data class GoogleIdTokenData(
        val email: String,
        val given_name: String,
        val family_name: String
    )
}