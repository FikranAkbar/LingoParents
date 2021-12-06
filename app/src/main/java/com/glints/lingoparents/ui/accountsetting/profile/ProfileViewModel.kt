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
import com.google.android.material.snackbar.Snackbar
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
        //amin
        tokenPreferences.resetAccessEmail()
        tokenPreferences.resetAccessPassword()
    }


    private fun onApiCallStarted() = viewModelScope.launch {
        profileChannel.send(ProfileEvent.Loading)
    }

    private fun onApiCallSuccess(parentProfile: ParentProfileResponse) = viewModelScope.launch {
        profileChannel.send(ProfileEvent.Success(parentProfile))
    }

    //amin
    private fun onEditApiCallSuccess() =
        viewModelScope.launch {
            profileChannel.send(ProfileEvent.editSuccess)
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
        object editSuccess : ProfileEvent()
        data class TryToEditProfile(
            val firstname: String,
            val lastname: String,
            val address: String,
            val phone: String
        ) : ProfileEvent()


    }

    fun getAccessToken(): LiveData<String> = tokenPreferences.getAccessToken().asLiveData()
    fun getAccessEmail(): LiveData<String> = tokenPreferences.getAccessEmail().asLiveData()

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

    //amin put
    fun editParentProfile(
        accessToken: String,
        firstname: String,
        lastname: String,
        address: String,
        phone: String
    ) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .editParentProfile(accessToken, firstname, lastname, address, phone)
            .enqueue(object : Callback<EditParentProfileResponse> {
                override fun onResponse(
                    call: Call<EditParentProfileResponse>,
                    response: Response<EditParentProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        onEditApiCallSuccess()
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<EditParentProfileResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    //kebawah amin
    fun onSaveButtonClick(
        firstname: String,
        lastname: String,
        address: String,
        phone: String
    ) = viewModelScope.launch {
        profileChannel.send(
            ProfileEvent.TryToEditProfile(
                firstname,
                lastname,
                address,
                phone
            )
        )
    }
}