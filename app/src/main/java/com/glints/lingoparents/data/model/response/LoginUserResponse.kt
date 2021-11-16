package com.glints.lingoparents.data.model.response

import com.google.gson.annotations.SerializedName

class LoginUserResponse {
    val success: String? = null
    val code: Int? = null
    val message: LoginUserDataResponse? = null

    data class LoginUserDataResponse(
        val accessToken: String,
        val refreshToken: String
    )
}


