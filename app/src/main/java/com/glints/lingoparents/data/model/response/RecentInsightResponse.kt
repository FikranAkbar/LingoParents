package com.glints.lingoparents.data.model.response

import com.google.gson.annotations.SerializedName

data class RecentInsightResponse(

	@field:SerializedName("message")
	val message: List<MessageItem>,

	@field:SerializedName("status")
	val status: String
)

data class MessageItem(

	@field:SerializedName("total_like")
	val totalLike: Int,

	@field:SerializedName("is_active")
	val isActive: String,

	@field:SerializedName("total_dislike")
	val totalDislike: Int,

	@field:SerializedName("idUser_update")
	val idUserUpdate: Int,

	@field:SerializedName("total_views")
	val totalViews: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("content")
	val content: String,

	@field:SerializedName("cover")
	val cover: String? = null,
//	val cover: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("total_report")
//	val totalReport: Any,
	val totalReport: Int,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("idUser_create")
	val idUserCreate: Int,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)
