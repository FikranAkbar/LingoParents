package com.glints.lingoparents.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.model.response.StudentListResponse
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProgressViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    private val progressEventChannel = Channel<ProgressEvent>()
    val progressEvent = progressEventChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        progressEventChannel.send(ProgressEvent.Loading)
    }

    private fun onApiCallSuccess(result: List<StudentListResponse.DataItem>) = viewModelScope.launch {
        progressEventChannel.send(ProgressEvent.Success(result))
    }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        progressEventChannel.send(ProgressEvent.Error(message))
    }

    sealed class ProgressEvent {
        object Loading : ProgressEvent()
        data class Success(val result: List<StudentListResponse.DataItem>) : ProgressEvent()
        data class Error(val message: String) : ProgressEvent()
    }
}