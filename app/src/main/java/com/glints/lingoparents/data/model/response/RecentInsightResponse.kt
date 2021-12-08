package com.glints.lingoparents.data.model.response

import com.google.gson.annotations.SerializedName

data class RecentInsightResponse(

    @field:SerializedName("message")
    val message: MutableList<RecentInsightItem>,

    @field:SerializedName("status")
    val status: String
)

data class RecentInsightItem(

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("content")
    val content: String,

    @field:SerializedName("cover")
    val cover: String? = null,
//	val cover: String,

    @field:SerializedName("id")
    val id: Int,
)
