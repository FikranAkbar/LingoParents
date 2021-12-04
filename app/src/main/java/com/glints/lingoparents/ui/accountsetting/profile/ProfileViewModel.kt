package com.glints.lingoparents.ui.accountsetting.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.EditParentProfileResponse
import com.glints.lingoparents.data.model.response.LiveEventDetailResponse
import com.glints.lingoparents.data.model.response.ParentProfileResponse
import com.glints.lingoparents.data.model.response.RegisterUserResponse
import com.glints.lingoparents.ui.register.RegisterViewModel
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        //amin
        data class TryToEditProfile(
            val firstName: String,
            val lastName: String,
            val phone: String,
            val address: String
        ) : ProfileEvent()


    }

    fun getAccessToken(): LiveData<String> = tokenPreferences.getAccessToken().asLiveData()

    //amin get

    fun getParentProfile(accessToken: String) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getParentProfile(accessToken)
            .enqueue(object : Callback<ParentProfileResponse> {
                override fun onResponse(
                    call: Call<ParentProfileResponse>,
                    response: Response<ParentProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()!!
                        onApiCallSuccess(result)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<ParentProfileResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

}