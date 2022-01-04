package com.glints.lingoparents.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.RegisterUserResponse
import com.glints.lingoparents.ui.REGISTER_USER_RESULT_OK
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.JWTUtils
import com.google.android.gms.auth.api.credentials.IdToken
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(
    private val googleIdToken: String? = null
) : ViewModel() {
    companion object {
        const val GOOGLE_ID_TOKEN_KEY = "idToken"
    }

    val googleFirstName: String
    val googleLastName: String
    val googleEmail: String

    init {
        if (googleIdToken != null && googleIdToken.isNotEmpty()) {
            println("Google Id Token: $googleIdToken")
            val data = JWTUtils.getDataFromGoogleIdToken(googleIdToken)
            data.apply {
                googleFirstName = given_name
                googleLastName = family_name
                googleEmail = email
            }
        }
        else {
            googleFirstName = ""
            googleLastName = ""
            googleEmail = ""
        }

        println("Google First Name: $googleFirstName")
        println("Google Last Name: $googleLastName")
        println("Google Email: $googleEmail")
    }

    private val registerEventChannel = Channel<RegisterEvent>()
    val registerEvent = registerEventChannel.receiveAsFlow()

    fun onSubmitButtonClick(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phone: String,
    ) = viewModelScope.launch {
        registerEventChannel.send(
            RegisterEvent.TryToRegisterUser(
                firstName= firstName,
                lastName= lastName,
                email= email,
                password= password,
                phone= phone
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
    ) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .registerUser(email, firstName, lastName, password, phone)
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
            val role: String = "parent"
        ) : RegisterEvent()
        object Loading : RegisterEvent()
        object Success : RegisterEvent()
        data class Error(val message: String) : RegisterEvent()
    }
}