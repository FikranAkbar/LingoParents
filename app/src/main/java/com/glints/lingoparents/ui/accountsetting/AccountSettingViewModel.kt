package com.glints.lingoparents.ui.accountsetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.EditParentProfileResponse
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountSettingViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    private val accountSettingChannel = Channel<AccountSetting>()
    val accountSettingEvent = accountSettingChannel.receiveAsFlow()

    fun loadingState() = viewModelScope.launch {
        accountSettingChannel.send(AccountSetting.Loading)
    }

    private fun onApiCallStarted() = viewModelScope.launch {
        accountSettingChannel.send(AccountSetting.Loading)
    }

    private fun onEditApiCallSuccess() =
        viewModelScope.launch {
            accountSettingChannel.send(AccountSetting.UploadPhotoSuccess)
        }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        accountSettingChannel.send(AccountSetting.Error(message))
    }

    fun uploadPhoto(
        photo: MultipartBody.Part,
    ) {
        APIClient
            .service
            .editParentProfile(null, null, null, null, photo)
            .enqueue(object : Callback<EditParentProfileResponse> {
                override fun onResponse(
                    call: Call<EditParentProfileResponse>,
                    response: Response<EditParentProfileResponse>,
                ) {
                    if (response.isSuccessful) {
                        onEditApiCallSuccess()
                    } else {
                        val apiError = ErrorUtils.parseError(response)

                    }
                }

                override fun onFailure(call: Call<EditParentProfileResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    fun onCroppedImage(
        photo: MultipartBody.Part,
    ) = viewModelScope.launch {
        accountSettingChannel.send(
            AccountSetting.TryToUploadPhoto(
                photo
            )
        )
    }

    sealed class AccountSetting {
        object Loading : AccountSetting()
        object UploadPhotoSuccess : AccountSetting()
        data class TryToUploadPhoto(
            val photo: MultipartBody.Part,
        ) : AccountSetting()

        data class Error(val message: String) : AccountSetting()

    }
}