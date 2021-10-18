package com.glints.lingoparents.data.remote.api

import com.glints.lingoparents.data.remote.body.UserLogin
import com.glints.lingoparents.data.remote.response.UserLoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    fun loginUser(
        @Body userLogin: UserLogin
    ): Call<UserLoginResponse>
}