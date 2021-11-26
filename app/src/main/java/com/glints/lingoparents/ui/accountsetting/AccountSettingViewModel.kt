package com.glints.lingoparents.ui.accountsetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AccountSettingViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    private val profileChannel = Channel<ProfileEvent>()
    val profileEvent = profileChannel.receiveAsFlow()

    fun onLogOutButtonClick() = viewModelScope.launch {
        profileChannel.send(ProfileEvent.NavigateToAuthScreen)
        tokenPreferences.resetAccessToken()
    }

    sealed class ProfileEvent {
        object NavigateToAuthScreen : ProfileEvent()
    }
}