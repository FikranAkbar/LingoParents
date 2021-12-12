package com.glints.lingoparents.data.model.response

import com.google.gson.annotations.SerializedName

data class SessionDetailBySessionIdResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) {
	data class Data(

		@field:SerializedName("session_level")
		val sessionLevel: String? = null,

		@field:SerializedName("session_title")
		val sessionTitle: String? = null,

		@field:SerializedName("cover_flag")
		val coverFlag: String? = null,

		@field:SerializedName("assignment_feedback")
		val assignmentFeedback: String? = null,

		@field:SerializedName("notes")
		val notes: String? = null,

		@field:SerializedName("assignments")
		val assignments: List<AssignmentsItem>? = null,

		@field:SerializedName("tutor_name")
		val tutorName: String? = null,

		@field:SerializedName("session_schedule")
		val sessionSchedule: String? = null,

		@field:SerializedName("session_sublevel")
		val sessionSublevel: String? = null,

		@field:SerializedName("student_score")
		val studentScore: Double? = null,

		@field:SerializedName("session_feedback")
		val sessionFeedback: String? = null,

		@field:SerializedName("attendance")
		val attendance: String? = null
	)

	data class AssignmentsItem(

		@field:SerializedName("question")
		val question: String? = null,

		@field:SerializedName("correct_answer")
		val correctAnswer: String? = null,

		@field:SerializedName("student_answer")
		val studentAnswer: String? = null,

		@field:SerializedName("id")
		val id: Int? = null
	)
}