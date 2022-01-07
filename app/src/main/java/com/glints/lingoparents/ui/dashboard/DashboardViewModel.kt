package com.glints.lingoparents.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DashboardViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    private val dashboardChannel = Channel<DashboardEvent>()
    val dashboardEvent = dashboardChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        dashboardChannel.send(DashboardEvent.Loading)
    }

    private fun onApiCallSuccess() = viewModelScope.launch {
        dashboardChannel.send(DashboardEvent.Success)
    }

    private fun onApiCallFailed(message: String) = viewModelScope.launch {
        dashboardChannel.send(DashboardEvent.Failed(message))
    }

    fun logoutUser() = viewModelScope.launch {
        onApiCallStarted()
        onApiCallSuccess()
        /*
        APIClient
            .service
            .logoutUser()
            .enqueue(object : Callback<LogoutUserResponse> {
                override fun onResponse(
                    call: Call<LogoutUserResponse>,
                    response: Response<LogoutUserResponse>
                ) {
                    onApiCallSuccess()
                }

                override fun onFailure(call: Call<LogoutUserResponse>, t: Throwable) {
                    onApiCallFailed("Network Failed...")
                }
            })
         */

    }

    fun onRefreshTokenExpired() = viewModelScope.launch {
        dashboardChannel.send(DashboardEvent.HandleRefreshTokenExpired)
    }

    fun onNoInternetAccessOrError(errorMessage: String) = viewModelScope.launch {
        dashboardChannel.send(DashboardEvent.NoInternetAccessOrSomethingError(errorMessage))
    }

    fun resetToken() = viewModelScope.launch {
        tokenPreferences.resetToken()
    }

    sealed class DashboardEvent {
        object HandleRefreshTokenExpired : DashboardEvent()
        object Loading : DashboardEvent()
        object Success : DashboardEvent()
        data class Failed(val message: String) : DashboardEvent()
        data class NoInternetAccessOrSomethingError(val message: String) : DashboardEvent()
    }
}