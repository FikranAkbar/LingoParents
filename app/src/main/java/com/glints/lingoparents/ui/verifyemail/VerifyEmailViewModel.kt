package com.glints.lingoparents.ui.verifyemail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class VerifyEmailViewModel: ViewModel() {

    private val verifyEmailEventChannel = Channel<VerifyEmailEvent>()
    val verifyEmailEvent = verifyEmailEventChannel.receiveAsFlow()

    private fun onApiCallSuccess(message: String) = viewModelScope.launch {
        verifyEmailEventChannel.send(VerifyEmailEvent.Success(message))
    }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        verifyEmailEventChannel.send(VerifyEmailEvent.Error(message))
    }

    sealed class VerifyEmailEvent {
        data class Error(val message: String) : VerifyEmailEvent()
        data class Success(val message: String) : VerifyEmailEvent()
    }
}