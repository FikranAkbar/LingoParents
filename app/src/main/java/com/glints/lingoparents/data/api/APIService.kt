package com.glints.lingoparents.data.api

import com.glints.lingoparents.data.model.response.LoginUserResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface APIService {
    @FormUrlEncoded
    @POST("api/v1/login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginUserResponse>

    @FormUrlEncoded
    @POST
    fun registerUser(
        @Field("email") email: String,
        @Field("firstname") firstname: String,
        @Field("lastname") lastname: String,
        @Field("password") password: String,
        @Field("phone") phone: String,
        @Field("address") address: String,
        @Field("gender") gender: String = "Male",
        @Field("role") role: String = "parent"
    )
}