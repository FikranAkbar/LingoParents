package com.glints.lingoparents.data.api

import com.glints.lingoparents.data.model.response.LiveEventDetailResponse
import com.glints.lingoparents.data.model.response.LiveEventListResponse
import com.glints.lingoparents.data.model.response.LoginUserResponse
import com.glints.lingoparents.data.model.response.RegisterUserResponse
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

    @GET("api/v1/events/all/{status}")
    fun getLiveEventsByStatus(
        @Path("status") status: String
    ): Call<LiveEventListResponse>

    @GET("api/v1/events/{id}")
    fun getLiveEventById(
        @Path("id") id: Int
    ): Call<LiveEventDetailResponse>
}