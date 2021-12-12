package com.glints.lingoparents.ui.progress.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.CourseDetailByStudentIdResponse
import com.glints.lingoparents.data.model.response.SessionDetailBySessionIdResponse
import com.glints.lingoparents.utils.ErrorUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProgressLearningCourseViewModel(
    private val studentId: Int,
    private val courseId: Int
) : ViewModel() {
    companion object {
        const val COURSE_ID_KEY = "course_id"
        const val STUDENT_ID_KEY = "student_id"
    }

    private val progressLearningCourseEventChannel = Channel<ProgressLearningCourseEvent>()
    val progressLearningCourseEvent = progressLearningCourseEventChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        progressLearningCourseEventChannel.send(ProgressLearningCourseEvent.Loading)

    }

    private fun onApiCallSuccess(
        courseDetail: CourseDetailByStudentIdResponse.Data,
        lastSessionDetail: SessionDetailBySessionIdResponse.Data,
        studentId: Int
    ) =
        viewModelScope.launch {
            progressLearningCourseEventChannel.send(ProgressLearningCourseEvent.Success(courseDetail, lastSessionDetail, studentId))
        }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        progressLearningCourseEventChannel.send(ProgressLearningCourseEvent.Error(message))
    }

    fun getCourseDetailByStudentId() = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getCourseDetailByStudentId(studentId, courseId)
            .enqueue(object : Callback<CourseDetailByStudentIdResponse> {
                override fun onResponse(
                    call: Call<CourseDetailByStudentIdResponse>,
                    response: Response<CourseDetailByStudentIdResponse>
                ) {
                    if (response.isSuccessful) {
                        getSessionDetailBySessionId(response.body()?.data!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<CourseDetailByStudentIdResponse>, t: Throwable) {
                    onApiCallError("Network failed..")
                }
            })
    }

    private fun getSessionDetailBySessionId(courseDetail: CourseDetailByStudentIdResponse.Data) =
        viewModelScope.launch {
            APIClient
                .service
                .getSessionDetailBySessionId(studentId, courseDetail.currentSession!!)
                .enqueue(object : Callback<SessionDetailBySessionIdResponse> {
                    override fun onResponse(
                        call: Call<SessionDetailBySessionIdResponse>,
                        response: Response<SessionDetailBySessionIdResponse>
                    ) {
                        if (response.isSuccessful) {
                            onApiCallSuccess(courseDetail, response.body()?.data!!, studentId)
                        }
                        else {
                            val apiError = ErrorUtils.parseError(response)
                            onApiCallError(apiError.message())
                        }
                    }

                    override fun onFailure(
                        call: Call<SessionDetailBySessionIdResponse>,
                        t: Throwable
                    ) {
                        onApiCallError("Network Failed..")
                    }
                })
        }

    sealed class ProgressLearningCourseEvent {
        object Loading : ProgressLearningCourseEvent()
        data class Success(
            val courseDetail: CourseDetailByStudentIdResponse.Data,
            val lastSessionDetail: SessionDetailBySessionIdResponse.Data,
            val studentId: Int
        ) : ProgressLearningCourseEvent()

        data class Error(val message: String) : ProgressLearningCourseEvent()
    }
}