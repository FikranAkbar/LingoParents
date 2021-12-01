package com.glints.lingoparents.data.api

import com.glints.lingoparents.data.model.response.*
import retrofit2.Call
import retrofit2.http.*

interface APIService {
    @FormUrlEncoded
    @POST("api/v1/login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginUserResponse>

    @FormUrlEncoded
    @POST("api/v1/register")
    fun registerUser(
        @Field("email") email: String,
        @Field("firstname") firstname: String,
        @Field("lastname") lastname: String,
        @Field("password") password: String,
        @Field("phone") phone: String,
        @Field("address") address: String = "_",
        @Field("gender") gender: String = "Male",
        @Field("role") role: String = "parent"
    ): Call<RegisterUserResponse>

    @FormUrlEncoded
    @POST("api/v1/forgot-password")
    fun sendForgotPasswordRequest(
        @Field("email") email: String,
        @Field("frontend_url") url: String = "http://localhost:3000/api/v1/reset-password"
    ): Call<ForgotPasswordResponse>

    @GET("api/v1/events/participants/pages")
    fun getLiveEventsByStatus(
        @QueryMap options: Map<String, String>,
        @Header("authorization") authorization: String
    ): Call<LiveEventListResponse>

    @GET("api/v1/events/participants/{id}")
    fun getLiveEventById(
        @Path("id") id: Int,
        @Header("authorization") authorization: String
    ): Call<LiveEventDetailResponse>
}