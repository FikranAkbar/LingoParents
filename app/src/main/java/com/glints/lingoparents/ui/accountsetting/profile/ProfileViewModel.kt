package com.glints.lingoparents.ui.accountsetting.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.EditParentProfileResponse
import com.glints.lingoparents.data.model.response.ParentProfileResponse
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    private val profileChannel = Channel<ProfileEvent>()
    val profileEvent = profileChannel.receiveAsFlow()

    fun onLogOutButtonClick() = viewModelScope.launch {
        profileChannel.send(ProfileEvent.NavigateToAuthScreen)
        tokenPreferences.resetToken()
    }


    private fun onApiCallStarted() = viewModelScope.launch {
        profileChannel.send(ProfileEvent.Loading)
    }

    private fun onApiCallSuccess(parentProfile: ParentProfileResponse) = viewModelScope.launch {
        profileChannel.send(ProfileEvent.Success(parentProfile))
        profileChannel.send(ProfileEvent.SendToAccountSetting(parentProfile))
    }

    private fun onEditApiCallSuccess() =
        viewModelScope.launch {
            profileChannel.send(ProfileEvent.EditSuccess)
        }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        profileChannel.send(ProfileEvent.Error(message))
    }

    fun getAccessToken(): LiveData<String> = tokenPreferences.getAccessToken().asLiveData()

    fun getParentProfile() = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getParentProfile()
            .enqueue(object : Callback<ParentProfileResponse> {
                override fun onResponse(
                    call: Call<ParentProfileResponse>,
                    response: Response<ParentProfileResponse>,
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()!!
                        onApiCallSuccess(result)
                    } else {
                        if (response.code() != 400) {
                            val apiError = ErrorUtils.parseError(response)
                            onApiCallError(apiError.message())
                        } else {
                            onApiCallError("Something went wrong...")
                        }
                    }
                }

                override fun onFailure(call: Call<ParentProfileResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    fun editParentProfile(
        firstname: RequestBody,
        lastname: RequestBody,
        address: RequestBody,
        phone: RequestBody,
    ) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .editParentProfile(firstname, lastname, address, phone, null)
            .enqueue(object : Callback<EditParentProfileResponse> {
                override fun onResponse(
                    call: Call<EditParentProfileResponse>,
                    response: Response<EditParentProfileResponse>,
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


    fun onSaveButtonClick(
        firstname: RequestBody,
        lastname: RequestBody,
        address: RequestBody,
        phone: RequestBody,
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

    fun sendParentDataToAccountSettingFragment(event: EventBusActionToAccountSetting) =
        viewModelScope.launch {
            EventBus.getDefault().post(event)
        }

    sealed class ProfileEvent {
        object NavigateToAuthScreen : ProfileEvent()
        object Loading : ProfileEvent()
        data class Success(val parentProfile: ParentProfileResponse) : ProfileEvent()
        data class SendToAccountSetting(val parentProfile: ParentProfileResponse) : ProfileEvent()
        data class Error(val message: String) : ProfileEvent()
        object EditSuccess : ProfileEvent()
        data class TryToEditProfile(
            val firstname: RequestBody,
            val lastname: RequestBody,
            val address: RequestBody,
            val phone: RequestBody,
        ) : ProfileEvent()
    }

    sealed class EventBusActionToAccountSetting {
        data class SendParentData(val parentProfile: ParentProfileResponse) :
            EventBusActionToAccountSetting()
    }
}