package com.glints.lingoparents.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SplashViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    private val splashEventChannel = Channel<SplashEvent>()
    val splashEvent = splashEventChannel.receiveAsFlow()

    fun sendNavigateToHomeScreenEvent() = viewModelScope.launch {
        delay(2000)
        splashEventChannel.send(SplashEvent.NavigateToHomeScreen)
    }

    fun sendNavigateToAuthScreenEvent() = viewModelScope.launch {
        delay(2000)
        splashEventChannel.send(SplashEvent.NavigateToAuthScreen)
    }

    fun sendNavigateToForgotPasswordEvent() = viewModelScope.launch {
        delay(2000)
        splashEventChannel.send(SplashEvent.NavigateToResetPasswordScreen)
    }

    fun getAccessToken(): LiveData<String> = tokenPreferences.getAccessToken().asLiveData()

    sealed class SplashEvent {
        object NavigateToHomeScreen : SplashEvent()
        object NavigateToAuthScreen : SplashEvent()
        object NavigateToResetPasswordScreen : SplashEvent()
    }
}