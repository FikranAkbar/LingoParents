package com.glints.lingoparents.data.model.response

class AllCoursesResponse {
    val status: String? = null
    val message: String? = null
    val data: List<CourseItemResponse>? = null

    data class CourseItemResponse(
        val id: Int,
        val title: String,
        val cover_flag: String
    )
}