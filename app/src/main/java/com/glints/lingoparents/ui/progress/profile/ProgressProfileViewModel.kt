package com.glints.lingoparents.ui.progress.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.StudentProfileResponse
import com.glints.lingoparents.utils.ErrorUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProgressProfileViewModel : ViewModel() {
    private val progressProfileEventChannel = Channel<ProgressProfileEvent>()
    val progressProfileEvent = progressProfileEventChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        progressProfileEventChannel.send(ProgressProfileEvent.Loading)
    }

    private fun onApiCallSuccess(result: StudentProfileResponse.Data) = viewModelScope.launch {
        progressProfileEventChannel.send(ProgressProfileEvent.Success(result))
    }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        progressProfileEventChannel.send(ProgressProfileEvent.Error(message))
    }

    fun getStudentProfileByStudentId(id: Int) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getStudentProfileById(id)
            .enqueue(object : Callback<StudentProfileResponse> {
                override fun onResponse(
                    call: Call<StudentProfileResponse>,
                    response: Response<StudentProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()?.data!!
                        onApiCallSuccess(result)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<StudentProfileResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    sealed class ProgressProfileEvent {
        object Loading : ProgressProfileEvent()
        data class Success(val result: StudentProfileResponse.Data) : ProgressProfileEvent()
        data class Error(val message: String) : ProgressProfileEvent()
    }
}