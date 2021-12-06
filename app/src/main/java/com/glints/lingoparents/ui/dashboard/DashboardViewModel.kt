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

    fun onRefreshTokenExpired() = viewModelScope.launch {
        dashboardChannel.send(DashboardEvent.HandleRefreshTokenExpired)
        tokenPreferences.resetToken()
    }

    sealed class DashboardEvent {
        object HandleRefreshTokenExpired : DashboardEvent()
    }
}