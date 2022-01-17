package com.glints.lingoparents.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.LoginUserResponse
import com.glints.lingoparents.data.model.response.RegisterUserResponse
import com.glints.lingoparents.ui.authentication.REGISTER_USER_RESULT_OK
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.JWTUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(
    private val tokenPreferences: TokenPreferences,
    googleIdToken: String? = null,
) : ViewModel() {
    companion object {
        const val GOOGLE_ID_TOKEN_KEY = "idToken"
    }

    val googleFirstName: String
    val googleLastName: String
    val googleEmail: String

    init {
        if (googleIdToken != null && googleIdToken.isNotEmpty()) {
            val data = JWTUtils.getDataFromGoogleIdToken(googleIdToken)
            data.apply {
                googleFirstName = given_name
                googleLastName = family_name
                googleEmail = email
            }
        } else {
            googleFirstName = ""
            googleLastName = ""
            googleEmail = ""
        }
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
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password,
                phone = phone
            )
        )
    }

    fun onLoginButtonClick() = viewModelScope.launch {
        registerEventChannel.send(RegisterEvent.NavigateBackToLogin)
    }

    private fun onApiCallStarted() = viewModelScope.launch {
        registerEventChannel.send(RegisterEvent.Loading)
    }

    private fun onRegisterApiCallSuccess(email: String, password: String) = viewModelScope.launch {
        registerEventChannel.send(RegisterEvent.RegisterSuccess(email, password))
    }

    private fun onLoginApiCallSuccess() = viewModelScope.launch {
        registerEventChannel.send(RegisterEvent.LoginSuccess)
    }

    private fun onRegisterApiCallError(message: String) = viewModelScope.launch {
        registerEventChannel.send(RegisterEvent.RegisterError(message))
    }

    private fun onLoginApiCallError(message: String) = viewModelScope.launch {
        registerEventChannel.send(RegisterEvent.LoginError(message))
    }

    private fun saveToken(accessToken: String, refreshToken: String) = viewModelScope.launch {
        tokenPreferences.resetToken()
        tokenPreferences.saveAccessToken(accessToken)
        tokenPreferences.saveRefreshToken(refreshToken)
    }

    private fun saveUserId(id: String) = viewModelScope.launch {
        tokenPreferences.saveUserId(id)
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
                    response: Response<RegisterUserResponse>,
                ) {
                    if (response.isSuccessful) {
                        onRegisterApiCallSuccess(email, password)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onRegisterApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<RegisterUserResponse>, t: Throwable) {
                    onRegisterApiCallError("Network Failed...")
                }
            })
    }

    fun loginAfterSuccessfulRegister(
        email: String,
        password: String,
    ) = viewModelScope.launch {
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
                        val accessToken = response.body()?.data?.accessToken.toString()
                        val refreshToken = response.body()?.data?.refreshToken.toString()

                        val userId = JWTUtils.getIdFromAccessToken(accessToken)
                        saveToken(accessToken, refreshToken)
                        saveUserId(userId)

                        onLoginApiCallSuccess()
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onLoginApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<LoginUserResponse>, t: Throwable) {
                    onLoginApiCallError("Network Failed...")
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
            val role: String = "parent",
        ) : RegisterEvent()
        object Loading : RegisterEvent()
        data class RegisterSuccess(val email: String, val password: String) : RegisterEvent()
        object LoginSuccess : RegisterEvent()
        data class RegisterError(val message: String) : RegisterEvent()
        data class LoginError(val message: String) : RegisterEvent()
    }
}