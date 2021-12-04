package com.glints.lingoparents.ui.accountsetting.changepassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.ChangePasswordResponse
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordSettingViewModel(
    private val tokenPref: TokenPreferences
) : ViewModel() {
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

        //amin
        data class TryToChangePassword(
            val currentPassword: String,
            val password: String,
            val newPassword: String
        ) : PasswordSettingViewModel.PasswordSettingEvent()
    }

    //amin
    fun onSubmitButtonClick(
        currentPassword: String,
        password: String,
        newPassword: String
    ) = viewModelScope.launch {
        passwordSettingChannel.send(
            PasswordSettingEvent.TryToChangePassword(
                currentPassword,
                password,
                newPassword
            )
        )
    }

    fun changePassword(
        id: Int,
        currentPassword: String,
        password: String,
        newPassword: String
    ) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .changePassword(id, currentPassword, password, newPassword)
            .enqueue(object : Callback<ChangePasswordResponse> {
                override fun onResponse(
                    call: Call<ChangePasswordResponse>,
                    response: Response<ChangePasswordResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess()
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    fun getAccessToken(): LiveData<String> = tokenPref.getAccessToken().asLiveData()

    //fun getCurrentAccountId(): Int = accountId


}