package com.glints.lingoparents.data.model.response

class LoginUserResponse {
    val success: String? = null
    val message: String? = null
    val data: LoginUserDataResponse? = null

    data class LoginUserDataResponse(
        val accessToken: String,
        val refreshToken: String
    )
}


