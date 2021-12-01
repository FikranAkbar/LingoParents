package com.glints.lingoparents.data.model.response

class LiveEventListResponse {
    val status: String? = null
    val message: String? = null
    val data: List<LiveEventItemResponse>? = null

    data class LiveEventItemResponse(
        val id: Int,
        val title: String,
        val date: String,
        val speaker_photo: String
    )
}