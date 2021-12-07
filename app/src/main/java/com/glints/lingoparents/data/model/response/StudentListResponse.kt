package com.glints.lingoparents.data.model.response

import com.google.gson.annotations.SerializedName

data class StudentListResponse(

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class DataItem(

	@field:SerializedName("level")
	val level: Any,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("sublevel")
	val sublevel: Any,

	@field:SerializedName("student_id")
	val studentId: Int,

	@field:SerializedName("photo")
	val photo: String,

	@field:SerializedName("relationship")
	val relationship: String,

	@field:SerializedName("age")
	val age: Int
)
