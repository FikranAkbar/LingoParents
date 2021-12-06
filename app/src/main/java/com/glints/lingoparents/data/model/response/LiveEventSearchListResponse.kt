package com.glints.lingoparents.data.model.response

class LiveEventSearchListResponse {
    val status: String? = null
    val message: String? = null
    val data: SearchListResponse? = null

    data class SearchListResponse(
        val count: Int,
        val rows: List<LiveEventListResponse.LiveEventItemResponse>
    )
}