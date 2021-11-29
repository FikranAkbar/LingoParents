package com.glints.lingoparents.ui.accountsetting.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.model.response.ParentProfileResponse
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    private val profileChannel = Channel<ProfileEvent>()
    val profileEvent = profileChannel.receiveAsFlow()

    fun onLogOutButtonClick() = viewModelScope.launch {
        profileChannel.send(ProfileEvent.NavigateToAuthScreen)
        tokenPreferences.resetAccessToken()
    }

    private fun onApiCallStarted() = viewModelScope.launch {
        profileChannel.send(ProfileEvent.Loading)
    }

    private fun onApiCallSuccess(parentProfile: ParentProfileResponse) = viewModelScope.launch {
        profileChannel.send(ProfileEvent.Success(parentProfile))
    }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        profileChannel.send(ProfileEvent.Error(message))
    }

    sealed class ProfileEvent {
        object NavigateToAuthScreen : ProfileEvent()
        object Loading : ProfileEvent()
        data class Success(val parentProfile: ParentProfileResponse) : ProfileEvent()
        data class Error(val message: String) : ProfileEvent()
    }
}