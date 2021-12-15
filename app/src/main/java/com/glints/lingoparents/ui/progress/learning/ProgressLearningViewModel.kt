package com.glints.lingoparents.ui.progress.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.CourseListByStudentIdResponse
import com.glints.lingoparents.utils.ErrorUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProgressLearningViewModel : ViewModel() {
    private val progressLearningEventChannel = Channel<ProgressLearningEvent>()
    val progressLearningEvent = progressLearningEventChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        progressLearningEventChannel.send(ProgressLearningEvent.Loading)
    }

    private fun onApiCallSuccess(result: List<CourseListByStudentIdResponse.DataItem>, studentId: Int) =
        viewModelScope.launch {
            progressLearningEventChannel.send(ProgressLearningEvent.Success(result, studentId))
        }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        progressLearningEventChannel.send(ProgressLearningEvent.Error(message))
    }

    fun getCourseListByStudentId(id: Int) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getCourseListByStudentId(id)
            .enqueue(object : Callback<CourseListByStudentIdResponse> {
                override fun onResponse(
                    call: Call<CourseListByStudentIdResponse>,
                    response: Response<CourseListByStudentIdResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.data!!.isNotEmpty()) {
                            val result = response.body()?.data!!
                            onApiCallSuccess(result, id)
                        }
                        else {
                            onApiCallError(response.body()?.message!!)
                        }

                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<CourseListByStudentIdResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    sealed class ProgressLearningEvent {
        object Loading : ProgressLearningEvent()
        data class Success(val result: List<CourseListByStudentIdResponse.DataItem>, val studentId: Int) :
            ProgressLearningEvent()

        data class Error(val message: String) : ProgressLearningEvent()
    }
}