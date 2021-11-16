package com.glints.lingoparents.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.LoginUserResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private val loginEventChannel = Channel<LoginEvent>()
    val loginEvent = loginEventChannel.receiveAsFlow()

    fun onLoginButtonClick(email: String, password: String) = viewModelScope.launch {
        loginUserByEmailPassword(email, password)
    }

    fun onForgotPasswordButtonClick() = viewModelScope.launch {
        loginEventChannel.send(LoginEvent.NavigateToForgotPassword)
    }

    fun onRegisterButtonClick() = viewModelScope.launch {
        loginEventChannel.send(LoginEvent.NavigateToRegister)
    }

    private fun onApiCallStarted() = viewModelScope.launch {
        loginEventChannel.send(LoginEvent.Loading)
    }

    private fun onApiCallSuccess(result: String) = viewModelScope.launch {
        loginEventChannel.send(LoginEvent.Success(result))
    }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        loginEventChannel.send(LoginEvent.Error(message))
    }

    private fun loginUserByEmailPassword(email: String, password: String) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .loginUser(email, password)
            .enqueue(object : Callback<LoginUserResponse> {
                override fun onResponse(
                    call: Call<LoginUserResponse>,
                    response: Response<LoginUserResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess("Login Successful")
                    } else {
                        onApiCallError("Login Failed...")
                    }
                }

                override fun onFailure(call: Call<LoginUserResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    sealed class LoginEvent {
        object NavigateToForgotPassword : LoginEvent()
        object NavigateToRegister : LoginEvent()
        object Loading : LoginEvent()
        data class Success(val result: String) : LoginEvent()
        data class Error(val message: String) : LoginEvent()
    }
}