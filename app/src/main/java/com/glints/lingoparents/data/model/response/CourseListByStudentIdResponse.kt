package com.glints.lingoparents.data.model.response

import com.google.gson.annotations.SerializedName

data class CourseListByStudentIdResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) {
	data class DataItem(

		@field:SerializedName("flag")
		val flag: String? = null,

		@field:SerializedName("id_course")
		val idCourse: Int? = null,

		@field:SerializedName("title")
		val title: String? = null
	)
}
