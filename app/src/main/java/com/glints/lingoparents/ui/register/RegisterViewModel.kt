package com.glints.lingoparents.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.RegisterUserResponse
import com.glints.lingoparents.ui.REGISTER_USER_RESULT_OK
import com.glints.lingoparents.utils.ErrorUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private val registerEventChannel = Channel<RegisterEvent>()
    val registerEvent = registerEventChannel.receiveAsFlow()

    fun onSubmitButtonClick(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phone: String,
        gender: String,
        address: String,
    ) = viewModelScope.launch {
        registerEventChannel.send(
            RegisterEvent.TryToRegisterUser(
                firstName= firstName,
                lastName= lastName,
                email= email,
                password= password,
                phone= phone,
                gender= gender,
                address= address
            )
        )
    }

    fun onLoginButtonClick() = viewModelScope.launch {
        registerEventChannel.send(RegisterEvent.NavigateBackToLogin)
    }

    fun onRegisterSuccessful() = viewModelScope.launch {
        registerEventChannel.send(RegisterEvent.NavigateBackWithResult(REGISTER_USER_RESULT_OK))
    }

    private fun onApiCallStarted() = viewModelScope.launch {
        registerEventChannel.send(RegisterEvent.Loading)
    }

    private fun onApiCallSuccess() = viewModelScope.launch {
        registerEventChannel.send(RegisterEvent.Success)
    }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        registerEventChannel.send(RegisterEvent.Error(message))
    }

    fun registerUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String,
        gender: String,
        address: String
    ) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .registerUser(email, firstName, lastName, password, phone, gender, address)
            .enqueue(object : Callback<RegisterUserResponse> {
                override fun onResponse(
                    call: Call<RegisterUserResponse>,
                    response: Response<RegisterUserResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess()
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<RegisterUserResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    sealed class RegisterEvent {
        object NavigateBackToLogin : RegisterEvent()
        data class NavigateBackWithResult(val result: Int) : RegisterEvent()
        data class TryToRegisterUser(
            val firstName: String,
            val lastName: String,
            val email: String,
            val password: String,
            val phone: String,
            val address: String,
            val gender: String,
            val role: String = "parent"
        ) : RegisterEvent()

        object Loading : RegisterEvent()
        object Success : RegisterEvent()
        data class Error(val message: String) : RegisterEvent()
    }
}