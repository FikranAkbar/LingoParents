package com.glints.lingoparents.ui.login

import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.LoginUserResponse
import com.glints.lingoparents.ui.REGISTER_USER_RESULT_OK
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

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

    private fun saveToken(token: String) = viewModelScope.launch {
        tokenPreferences.saveAccessToken(token)
    }

    //amin
    fun saveEmail(email: String) = viewModelScope.launch {
        tokenPreferences.saveAccessEmail(email)
    }

    fun savePassword(password: String) = viewModelScope.launch {
        tokenPreferences.saveAccessPassword(password)
    }

    fun saveParentId(parentId: Int) = viewModelScope.launch {
        tokenPreferences.saveAccessParentId(parentId)
    }

    fun decodeToken(jwt: String): String {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return "Requires SDK 26"
        val parts = jwt.split(".")
        return try {
            val charset = charset("UTF-8")
            val header =
                String(Base64.getUrlDecoder().decode(parts[0].toByteArray(charset)), charset)
            val payload =
                String(Base64.getUrlDecoder().decode(parts[1].toByteArray(charset)), charset)
            "$header"
            "$payload"
        } catch (e: Exception) {
            "Error parsing JWT: $e"
        }
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
                        saveToken(response.body()?.data?.accessToken.toString())
                        val mDecode = decodeToken(response.body()?.data?.accessToken.toString())
                        val test = (JSONObject(mDecode).getString("data"))
                        saveParentId(JSONObject(test).getString("id").toInt())

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