package com.glints.lingoparents.ui.verifyemail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.VerifyEmailResponse
import com.glints.lingoparents.utils.ErrorUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyEmailViewModel : ViewModel() {

    private val verifyEmailEventChannel = Channel<VerifyEmailEvent>()
    val verifyEmailEvent = verifyEmailEventChannel.receiveAsFlow()

    private fun onApiCallSuccess(message: String) = viewModelScope.launch {
        verifyEmailEventChannel.send(VerifyEmailEvent.Success(message))
    }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        verifyEmailEventChannel.send(VerifyEmailEvent.Error(message))
    }

    fun verifyUserEmail(token: String, id: String) = viewModelScope.launch {
        println("Token Verify Email: $token")
        println("Id Verify Email: $id")
        APIClient
            .service
            .verifyEmail(mapOf("token" to token, "id" to id))
            .enqueue(object : Callback<VerifyEmailResponse> {
                override fun onResponse(
                    call: Call<VerifyEmailResponse>,
                    response: Response<VerifyEmailResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess(response.body()!!.message)
                    } else {
                        val apiError = ErrorUtils.parseErrorWithStatusAsString(response)
                        onApiCallError(apiError.getMessage())
                    }
                }

                override fun onFailure(call: Call<VerifyEmailResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    sealed class VerifyEmailEvent {
        data class Error(val message: String) : VerifyEmailEvent()
        data class Success(val message: String) : VerifyEmailEvent()
    }
}