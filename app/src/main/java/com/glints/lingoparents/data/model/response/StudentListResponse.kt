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
	val level: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("sublevel")
	val sublevel: String,

	@field:SerializedName("student_id")
	val studentId: Int,

	@field:SerializedName("photo")
	val photo: String,

	@field:SerializedName("relationship")
	val relationship: String,

	@field:SerializedName("age")
	val age: Int
)

//class StudentListResponse {
//	val status: String? = null
//	val message: String? = null
//	val data: List<StudentListResponse.StudentListItemResponse>? = null
//
//	data class StudentListItemResponse(
//		val level: String,
//		val id: Int,
//		val title: String,
//		val cover_flag: String
//	)
//
//}