package com.glints.lingoparents.ui.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.ResetPasswordResponse
import com.glints.lingoparents.utils.ErrorUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordViewModel : ViewModel() {
    private val resetPasswordEventChannel = Channel<ResetPasswordEvent>()
    val resetPasswordEvent = resetPasswordEventChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        resetPasswordEventChannel.send(ResetPasswordEvent.Loading)
    }

    private fun onApiCallSuccess(message: String) = viewModelScope.launch {
        resetPasswordEventChannel.send(ResetPasswordEvent.Success(message))
    }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        resetPasswordEventChannel.send(ResetPasswordEvent.Error(message))
    }

    fun resetPassword(token: String, id: String, newPassword: String, newConfirmPassword: String) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .resetPassword(mapOf("token" to token, "id" to id), newPassword, newConfirmPassword)
            .enqueue(object : Callback<ResetPasswordResponse> {
                override fun onResponse(
                    call: Call<ResetPasswordResponse>,
                    response: Response<ResetPasswordResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess(response.body()?.message!!)
                    }
                    else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    sealed class ResetPasswordEvent {
        object Loading : ResetPasswordEvent()
        data class Success(val message: String) : ResetPasswordEvent()
        data class Error(val message: String) : ResetPasswordEvent()
    }
}