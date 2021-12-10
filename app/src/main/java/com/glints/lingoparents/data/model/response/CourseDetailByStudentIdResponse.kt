package com.glints.lingoparents.data.model.response

import com.google.gson.annotations.SerializedName

data class CourseDetailByStudentIdResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) {
	data class Data(

		@field:SerializedName("sessions")
		val sessions: List<SessionsItem>? = null,

		@field:SerializedName("hours_to_complete")
		val hoursToComplete: Int? = null,

		@field:SerializedName("current_session")
		val currentSession: Int? = null,

		@field:SerializedName("learning_hours")
		val learningHours: Int? = null,

		@field:SerializedName("progress")
		val progress: Int? = null,

		@field:SerializedName("overall_feedback")
		val overallFeedback: String? = null,

		@field:SerializedName("modules_to_complete")
		val modulesToComplete: Int? = null,

		@field:SerializedName("category")
		val category: String? = null,

		@field:SerializedName("level_list")
		val levelList: List<LevelListItem>? = null,

		@field:SerializedName("overall_score")
		val overallScore: Int? = null
	)

	data class LevelListItem(

		@field:SerializedName("Upper Intermediate English")
		val upperIntermediateEnglish: List<String>? = null,

		@field:SerializedName("Intermediate English")
		val intermediateEnglish: List<String>? = null,

		@field:SerializedName("Elementary English")
		val elementaryEnglish: List<String>? = null,

		@field:SerializedName("Beginner English")
		val beginnerEnglish: List<String>? = null
	)

	data class SessionsItem(

		@field:SerializedName("feedback")
		val feedback: String? = null,

		@field:SerializedName("session_title")
		val sessionTitle: String? = null,

		@field:SerializedName("score")
		val score: Int? = null,

		@field:SerializedName("id_session")
		val idSession: Int? = null,

		@field:SerializedName("module_completed")
		val moduleCompleted: String? = null,

		@field:SerializedName("attendance")
		val attendance: String? = null
	)
}


