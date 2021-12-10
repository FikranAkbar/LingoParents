package com.glints.lingoparents.ui.progress.learning

import androidx.lifecycle.ViewModel

class ProgressLearningCourseViewModel(
    private val studentId: Int,
    private val courseId: Int
) : ViewModel() {
    companion object {
        const val COURSE_ID_KEY = "course_id"
        const val STUDENT_ID_KEY = "student_id"
    }

    sealed class ProgressLearningCourseEvent {

    }
}