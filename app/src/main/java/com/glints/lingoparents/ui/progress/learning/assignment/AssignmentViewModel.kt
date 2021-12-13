package com.glints.lingoparents.ui.progress.learning.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.SessionDetailBySessionIdResponse
import com.glints.lingoparents.utils.ErrorUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssignmentViewModel(
    private val studentId: Int,
    private val sessionId: Int
) : ViewModel() {
    companion object {
        const val STUDENT_ID_KEY = "studentId"
        const val SESSION_ID_KEY = "sessionId"
    }

    private val AssignmentEventChannel = Channel<AssignmentEvent>()
    val assignmentEvent = AssignmentEventChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        AssignmentEventChannel.send(AssignmentEvent.Loading)
    }

    private fun onApiCallSuccess(result: SessionDetailBySessionIdResponse.Data) = viewModelScope.launch {
        AssignmentEventChannel.send(AssignmentEvent.Success(result))
    }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        AssignmentEventChannel.send(AssignmentEvent.Error(message))
    }

    fun getSessionDetailBySessionId() = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getSessionDetailBySessionId(studentId, sessionId)
            .enqueue(object : Callback<SessionDetailBySessionIdResponse> {
                override fun onResponse(
                    call: Call<SessionDetailBySessionIdResponse>,
                    response: Response<SessionDetailBySessionIdResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess(response.body()?.data!!)
                    }
                    else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<SessionDetailBySessionIdResponse>, t: Throwable) {
                    onApiCallError("Network Failed.. $t")
                }
            })
    }

    sealed class AssignmentEvent {
        object Loading : AssignmentEvent()
        data class Success(val result: SessionDetailBySessionIdResponse.Data) : AssignmentEvent()
        data class Error(val message: String) : AssignmentEvent()
    }
}