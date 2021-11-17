package com.glints.lingoparents.ui.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {
    private val forgotPasswordEventChannel = Channel<ForgotPasswordEvent>()
    val forgotPasswordEvent = forgotPasswordEventChannel.receiveAsFlow()

    fun onBackToLoginButtonClick() = viewModelScope.launch {
        forgotPasswordEventChannel.send(ForgotPasswordEvent.NavigateBackToLogin)
    }

    sealed class ForgotPasswordEvent {
        object NavigateBackToLogin : ForgotPasswordEvent()
    }
}