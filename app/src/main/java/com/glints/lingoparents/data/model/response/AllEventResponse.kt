package com.glints.lingoparents.data.model.response

import com.google.gson.annotations.SerializedName

data class AllEventResponse(

	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("data")
	val data: MutableList<AllEventItem>,

	@field:SerializedName("status")
	val status: String
)

data class AllEventItem(

	@field:SerializedName("date")
	val date: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("cover")
	val cover: String? = null,
	@field:SerializedName("speaker_photo")
	val speaker_photo: String? = null,
	@field:SerializedName("price")
	val price: Int,

	@field:SerializedName("id")
	val id: Int,
)
