package com.glints.lingoparents.ui.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.ForgotPasswordResponse
import com.glints.lingoparents.utils.ErrorUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordViewModel : ViewModel() {
    private val forgotPasswordEventChannel = Channel<ForgotPasswordEvent>()
    val forgotPasswordEvent = forgotPasswordEventChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        forgotPasswordEventChannel.send(ForgotPasswordEvent.Loading)
    }

    private fun onApiCallSuccess() = viewModelScope.launch {
        forgotPasswordEventChannel.send(ForgotPasswordEvent.Success)
    }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        forgotPasswordEventChannel.send(ForgotPasswordEvent.Error(message))
    }

    fun sendForgotPasswordRequest(email: String) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .sendForgotPasswordRequest(email)
            .enqueue(object : Callback<ForgotPasswordResponse> {
                override fun onResponse(
                    call: Call<ForgotPasswordResponse>,
                    response: Response<ForgotPasswordResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess()
                    }
                    else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    fun onBackToLoginButtonClick() = viewModelScope.launch {
        forgotPasswordEventChannel.send(ForgotPasswordEvent.NavigateBackToLogin)
    }

    fun onSubmitButtonClick(email: String) = viewModelScope.launch {
        forgotPasswordEventChannel.send(ForgotPasswordEvent.TryToSubmitForgotPassword(email))
    }


    sealed class ForgotPasswordEvent {
        object NavigateBackToLogin : ForgotPasswordEvent()
        data class TryToSubmitForgotPassword(val email: String) : ForgotPasswordEvent()
        object Loading : ForgotPasswordEvent()
        object Success : ForgotPasswordEvent()
        data class Error(val message: String) : ForgotPasswordEvent()
    }
}