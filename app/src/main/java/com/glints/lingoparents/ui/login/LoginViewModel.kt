package com.glints.lingoparents.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.LoginUserResponse
import com.glints.lingoparents.ui.REGISTER_USER_RESULT_OK
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.JWTUtils
import com.glints.lingoparents.utils.TokenPreferences
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
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

    fun onRegisterUserSuccessful(result: Int) = viewModelScope.launch {
        when (result) {
            REGISTER_USER_RESULT_OK -> showSnackBarMessage("Register User Successful!")
        }
    }

    fun onLoginWithGoogleClick() = viewModelScope.launch {
        loginEventChannel.send(LoginEvent.TryToLoginWithGoogle)
    }

    fun onLoginWithGoogleSuccessful(account: GoogleSignInAccount) = viewModelScope.launch {
        loginEventChannel.send(LoginEvent.LoginWithGoogleSuccess(account))
    }

    fun onLoginWithGoogleFailure(message: String) = viewModelScope.launch {
        loginEventChannel.send(LoginEvent.LoginWithGoogleFailure(message))
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

    private fun showSnackBarMessage(message: String) = viewModelScope.launch {
        loginEventChannel.send(LoginEvent.ShowSnackBarMessage(message))
    }

    private fun saveToken(accessToken: String, refreshToken: String) = viewModelScope.launch {
        tokenPreferences.saveAccessToken(accessToken)
        tokenPreferences.saveRefreshToken(refreshToken)
    }


    fun saveEmail(email: String) = viewModelScope.launch {
        tokenPreferences.saveAccessEmail(email)
    }

    fun saveUserId(id: String) = viewModelScope.launch {
        tokenPreferences.saveUserId(id)
    }

    fun loginUserByEmailPassword(email: String, password: String) = viewModelScope.launch {
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

                        val accessToken = response.body()?.data?.accessToken.toString()
                        val refreshToken = response.body()?.data?.refreshToken.toString()
                        val userId = JWTUtils.getIdFromAccessToken(accessToken)
                        saveToken(accessToken, refreshToken)
                        saveEmail(email)
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

    fun loginWithGoogleEmail(idToken: String) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .loginWithGoogle(idToken)
            .enqueue(object : Callback<LoginUserResponse> {
                override fun onResponse(
                    call: Call<LoginUserResponse>,
                    response: Response<LoginUserResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess("Login Successful")

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

    sealed class LoginEvent {
        object NavigateToForgotPassword : LoginEvent()
        object NavigateToRegister : LoginEvent()
        data class TryToLoginUser(val email: String, val password: String) : LoginEvent()
        object TryToLoginWithGoogle : LoginEvent()
        data class LoginWithGoogleSuccess(val account: GoogleSignInAccount) : LoginEvent()
        data class LoginWithGoogleFailure(val errorMessage: String) : LoginEvent()
        object Loading : LoginEvent()
        data class Success(val result: String) : LoginEvent()
        data class Error(val message: String) : LoginEvent()
        data class ShowSnackBarMessage(val message: String) : LoginEvent()
    }
}