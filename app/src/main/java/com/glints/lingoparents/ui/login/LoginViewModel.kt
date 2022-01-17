package com.glints.lingoparents.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.LoginUserResponse
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.JWTUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    private val loginEventChannel = Channel<LoginEvent>()
    val loginEvent = loginEventChannel.receiveAsFlow()

    fun onLoginButtonClick(email: String, password: String) = viewModelScope.launch {
        loginEventChannel.send(LoginEvent.TryToLoginUser(email, password))
    }

    fun onForgotPasswordButtonClick() = viewModelScope.launch {
        loginEventChannel.send(LoginEvent.NavigateToForgotPassword)
    }

    fun onRegisterButtonClick() = viewModelScope.launch {
        loginEventChannel.send(LoginEvent.NavigateToRegister)
    }

    fun onLoginWithGoogleClick() = viewModelScope.launch {
        loginEventChannel.send(LoginEvent.TryToLoginWithGoogle)
    }

    fun onLoginWithGoogleFailure(message: String) = viewModelScope.launch {
        loginEventChannel.send(LoginEvent.LoginWithGoogleFailure(message))
    }

    private fun onApiCallStarted() = viewModelScope.launch {
        loginEventChannel.send(LoginEvent.Loading)
    }

    private fun onApiCallSuccess() = viewModelScope.launch {
        loginEventChannel.send(LoginEvent.Success)
    }

    private fun onApiCallError(message: String, idToken: String? = null) = viewModelScope.launch {
        loginEventChannel.send(LoginEvent.Error(message, idToken))
    }

    private fun saveToken(accessToken: String, refreshToken: String) = viewModelScope.launch {
        tokenPreferences.resetToken()
        tokenPreferences.saveAccessToken(accessToken)
        tokenPreferences.saveRefreshToken(refreshToken)
    }

    private fun saveUserId(id: String) = viewModelScope.launch {
        tokenPreferences.saveUserId(id)
    }

    /**
     * Method to login user with manually fill the email and password.
     * @param email Email of user
     * @param password Password from user's email
     */
    fun loginUserByEmailPassword(email: String, password: String) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .loginUser(email, password)
            .enqueue(object : Callback<LoginUserResponse> {
                override fun onResponse(
                    call: Call<LoginUserResponse>,
                    response: Response<LoginUserResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess()

                        val accessToken = response.body()?.data?.accessToken.toString()
                        val refreshToken = response.body()?.data?.refreshToken.toString()

                        val userId = JWTUtils.getIdFromAccessToken(accessToken)
                        saveToken(accessToken, refreshToken)
                        saveUserId(userId)

                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<LoginUserResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    /**
     * Method to login user with google account.
     * If user already has account created, user will be redirected to dashboard activity.
     * Else, user will be redirected to register fragment with few fields already filled with the information from google id token
     * @param idToken Json Web Token received from google sign in account
     */
    fun loginWithGoogleEmail(idToken: String) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .loginWithGoogle(idToken)
            .enqueue(object : Callback<LoginUserResponse> {
                override fun onResponse(
                    call: Call<LoginUserResponse>,
                    response: Response<LoginUserResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess()

                        val accessToken = response.body()?.data?.accessToken.toString()
                        val refreshToken = response.body()?.data?.refreshToken.toString()
                        val userId = JWTUtils.getIdFromAccessToken(accessToken)
                        saveToken(accessToken, refreshToken)
                        saveUserId(userId)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message(), idToken)
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
        data class TryToLoginUser(val email: String, val password: String) : LoginEvent()
        object TryToLoginWithGoogle : LoginEvent()
        data class LoginWithGoogleFailure(val errorMessage: String) : LoginEvent()
        object Loading : LoginEvent()
        object Success : LoginEvent()
        data class Error(val message: String, val idToken: String?) : LoginEvent()
    }
}