package com.glints.lingoparents.ui.accountsetting.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PasswordSettingViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    private val passwordSettingChannel = Channel<PasswordSettingEvent>()
    val passwordSettingEvent = passwordSettingChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        passwordSettingChannel.send(PasswordSettingEvent.Loading)
    }

    private fun onApiCallSuccess() = viewModelScope.launch {
        passwordSettingChannel.send(PasswordSettingEvent.Success)
    }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        passwordSettingChannel.send(PasswordSettingEvent.Error(message))
    }

    sealed class PasswordSettingEvent {
        object Loading : PasswordSettingEvent()
        object Success : PasswordSettingEvent()
        data class Error(val message: String) : PasswordSettingEvent()
    }
}