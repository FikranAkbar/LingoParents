package com.glints.lingoparents.ui.progress.learning

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.CourseDetailByStudentIdResponse
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

    private fun onApiCallSuccess(result: CourseDetailByStudentIdResponse.Data) = viewModelScope.launch {
        progressLearningCourseEventChannel.send(ProgressLearningCourseEvent.Success(result))
    }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        progressLearningCourseEventChannel.send(ProgressLearningCourseEvent.Error(message))

    }

    fun getCourseDetailByStudentId() = viewModelScope.launch {
        onApiCallStarted()
        Log.d("APICALL:", "Started..")
        APIClient
            .service
            .getCourseDetailByStudentId(studentId, courseId)
            .enqueue(object : Callback<CourseDetailByStudentIdResponse> {
                override fun onResponse(
                    call: Call<CourseDetailByStudentIdResponse>,
                    response: Response<CourseDetailByStudentIdResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess(response.body()?.data!!)
                        Log.d("APICALL:", "Success..")
                    }
                    else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                        Log.d("APICALL:", "Failed.. ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<CourseDetailByStudentIdResponse>, t: Throwable) {
                    onApiCallError("Network failed..")
                }
            })
    }

    sealed class ProgressLearningCourseEvent {
        object Loading : ProgressLearningCourseEvent()
        data class Success(val result: CourseDetailByStudentIdResponse.Data) : ProgressLearningCourseEvent()
        data class Error(val message: String) : ProgressLearningCourseEvent()
    }
}