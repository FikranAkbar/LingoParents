package com.glints.lingoparents.data.api

import com.glints.lingoparents.data.model.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface APIService {
    @FormUrlEncoded
    @POST("api/v1/login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<LoginUserResponse>

    @FormUrlEncoded
    @POST("api/v1/google-login")
    fun loginWithGoogle(
        @Field("id_token") idToken: String,
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
        @Field("role") role: String = "parent",
        @Field("link_email") link: String = "https://fe-main.ipe-glintsacademy.com/verify-email"
    ): Call<RegisterUserResponse>

    @PUT("api/v1/verify-email")
    fun verifyEmail(
        @QueryMap options: Map<String, String>
    ): Call<VerifyEmailResponse>

    @FormUrlEncoded
    @POST("api/v1/forgot-password")
    fun sendForgotPasswordRequest(
        @Field("email") email: String,
        @Field("frontend_url") url: String = "https://fe-main.ipe-glintsacademy.com/reset-password",
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
        @Path("title") title: String,
    ): Call<LiveEventSearchListResponse>

    @GET("api/v1/events/parent/upcoming/{title}")
    fun getUpcomingLiveEventByStatusAndTitle(
        @Path("title") title: String,
    ): Call<LiveEventSearchListResponse>

    @GET("api/v1/events/parent/completed/{title}")
    fun getCompletedLiveEventByStatusAndTitle(
        @Path("title") title: String,
    ): Call<LiveEventSearchListResponse>

    @GET("api/v1/events/parent/{id}")
    fun getLiveEventById(
        @Path("id") id: Int,
    ): Call<LiveEventDetailResponse>


    @GET("api/v1/insights?status=Publish")
    fun getAllInsightList(
        @QueryMap options: Map<String, String>,
    ): Call<AllInsightsListResponse>

    @GET("api/v1/insights?status=Publish")
    fun getInsightSearchList(
        @QueryMap options: Map<String, String>,
    ): Call<AllInsightsListResponse>

    @GET("api/v1/insights/{id}")
    fun getInsightDetail(
        @Path("id") id: Int,
    ): Call<InsightDetailResponse>

    @FormUrlEncoded
    @POST("api/v1/insights/report")
    fun reportInsight(
        @QueryMap options: Map<String, String>,
        @Field("report_comment") report_comment: String,
    ): Call<ReportResponse>

    @POST("api/v1/insights/like/{id}/{type}")
    fun likeInsightDetail(
        @Path("id") id: Int,
        @Path("type") type: String,
    ): Call<InsightLikeDislikeResponse>

    @POST("api/v1/insights/dislike/{id}/{type}")
    fun dislikeInsightDetail(
        @Path("id") id: Int,
        @Path("type") type: String,
    ): Call<InsightLikeDislikeResponse>

    @FormUrlEncoded
    @POST("api/v1/insights/comment/{id}/{type}")
    fun createComment(
        @Path("id") id: Int,
        @Path("type") type: String,
        @Field("comment") comment: String,
    ): Call<CreateCommentResponse>

    @GET("api/v1/insights/comment/{id}")
    fun getCommentReplies(
        @Path("id") id: Int,
    ): Call<GetCommentRepliesResponse>

    @DELETE("api/v1/insights/comment/{id}")
    fun deleteComment(
        @Path("id") id: Int,
    ): Call<DeleteCommentResponse>

    @FormUrlEncoded
    @PATCH("api/v1/insights/comment/{id}")
    fun updateComment(
        @Path("id") id: Int,
        @Field("comment") comment: String,
    ): Call<UpdateCommentResponse>

    @GET("api/v1/courses")
    fun getCourseList(): Call<AllCoursesResponse>

    @GET("api/v1/parents/profile")
    fun getParentProfile(): Call<ParentProfileResponse>

    @Multipart
    @PUT("api/v1/parents/profile")
    fun editParentProfile(
        @Part("firstname") firstname: RequestBody?,
        @Part("lastname") lastname: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part("phone") phone: RequestBody?,
        @Part image: MultipartBody.Part?,
    ): Call<EditParentProfileResponse>

    @FormUrlEncoded
    @PUT("api/v1/parents/profile/change-password")
    fun changePassword(
        @Field("password") currentPassword: String,
        @Field("new_password") password: String,
        @Field("confirmpassword") confirmPassword: String,
    ): Call<ChangePasswordResponse>

    @GET("api/v1/students/list/{id}")
    fun getStudentList(
        @Path("id") id: Int,
    ): Call<StudentListResponse>

    @GET("api/v1/insights/recent")
    fun getRecentInsight(
    ): Call<RecentInsightResponse>

    @GET("api/v1/events/parent")
    fun getAllEvent(
    ): Call<AllEventResponse>

    @GET("api/v1/courses/{id}")
    fun getCourseDetail(
        @Path("id") id: Int,
    ): Call<DetailCourseResponse>

    @GET("api/v1/students/list/{id}")
    fun getStudentListByParentId(
        @Path("id") id: String,
    ): Call<StudentListResponse>

    @GET("api/v1/students/{id}")
    fun getStudentProfileById(
        @Path("id") id: Int,
    ): Call<StudentProfileResponse>

    @GET("api/v1/students/{id}/courses")
    fun getCourseListByStudentId(
        @Path("id") studentId: Int,
    ): Call<CourseListByStudentIdResponse>

    @GET("api/v1/students/{studentId}/courses/{courseId}")
    fun getCourseDetailByStudentId(
        @Path("studentId") studentId: Int,
        @Path("courseId") courseId: Int,
    ): Call<CourseDetailByStudentIdResponse>

    @GET("api/v1/students/characters/{characterId}")
    fun getStudentCharacter(
        @Path("characterId") characterId: Int,
    ): Call<StudentCharacterResponse>

    @GET("api/v1/students/{studentId}/session-detail/{sessionId}")
    fun getSessionDetailBySessionId(
        @Path("studentId") studentId: Int,
        @Path("sessionId") sessionId: Int,
    ): Call<SessionDetailBySessionIdResponse>

    @FormUrlEncoded
    @POST("api/v1/events/participants")
    fun registerLiveEvent(
        @Field("id_user") id_user: Int,
        @Field("id_event") id_event: Int,
        @Field("fullname") fullname: String,
        @Field("phone") phone: String,
        @Field("email") email: String,
        @Field("attendance") attendance: String,
        @Field("attendance_time") attendance_time: String,
        @Field("idUser_create") idUser_create: Int,
        @Field("total_prize") total_prize: Int,
        @Field("voucher_code") voucher_code: String,
        @Field("payment_method") payment_method: String,
        @Field("status") status: String,
    ): Call<LiveEventRegisterResponse>

    @POST("api/v1/event_orders/create_order")
    fun createEventOrder(
        @Body createOrderData: CreateOrderData
    ) : Call<CreateOrderResponse>
}