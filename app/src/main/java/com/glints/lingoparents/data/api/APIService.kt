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

    @POST("api/v1/logout")
    fun logoutUser(): Call<LogoutUserResponse>

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
        @Field("frontend_url") url: String = "http://fe-main.ipe-glintsacademy.com/api/v1/reset-password"
    ): Call<ForgotPasswordResponse>

    @FormUrlEncoded
    @POST("api/v1/password-reset")
    fun resetPassword(
        @QueryMap options: Map<String, String>,
        @Field("password") newPassword: String,
        @Field("confirmpassword") confirmNewPassword: String,
    ): Call<ResetPasswordResponse>

    @GET("api/v1/events/participants/pages")
    fun getLiveEventsByStatus(
        @QueryMap options: Map<String, String>,
    ): Call<LiveEventListResponse>

    @GET("api/v1/events/parent/live/{title}")
    fun getTodayLiveEventByStatusAndTitle(
        @Path("title") title: String
    ): Call<LiveEventSearchListResponse>

    @GET("api/v1/events/parent/upcoming/{title}")
    fun getUpcomingLiveEventByStatusAndTitle(
        @Path("title") title: String
    ): Call<LiveEventSearchListResponse>

    @GET("api/v1/events/parent/completed/{title}")
    fun getCompletedLiveEventByStatusAndTitle(
        @Path("title") title: String
    ): Call<LiveEventSearchListResponse>

    @GET("api/v1/events/participants/{id}")
    fun getLiveEventById(
        @Path("id") id: Int,
    ): Call<LiveEventDetailResponse>



    @GET("api/v1/insights?status=Publish")
    fun getAllInsightList(
        @QueryMap options: Map<String, String>,
        @Header("authorization") authorization: String
    ): Call<AllInsightsListResponse>

    //amin
    @GET("api/v1/courses")
    fun getCourseList(
        @Header("authorization") authorization: String
    ): Call<AllCoursesResponse>

    @GET("api/v1/parents/profile")
    fun getParentProfile(
        @Header("authorization") authorization: String
    ): Call<ParentProfileResponse>

    @FormUrlEncoded
    @PUT("api/v1/parents/profile")
    fun editParentProfile(
        @Header("authorization") authorization: String,
        @Field("firstname") firstname: String,
        @Field("lastname") lastname: String,
        @Field("address") address: String,
        @Field("phone") phone: String
    ): Call<EditParentProfileResponse>

    @FormUrlEncoded
    @PUT("api/v1/parents/profile/change-password")
    fun changePassword(
        @Header("authorization") authorization: String,
        @Field("password") currentPassword: String,
        @Field("new_password") password: String,
        @Field("confirmpassword") confirmPassword: String,
    ): Call<ChangePasswordResponse>


    @GET("api/v1/courses/{id}")
    fun getCourseDetail(
        @Path("id") id: Int,
        @Header("authorization") authorization: String
    ): Call<DetailCourseResponse>
}