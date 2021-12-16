package com.glints.lingoparents.ui.accountsetting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AccountSettingViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    private val accountSettingChannel = Channel<AccountSetting>()
    val accountSettingEvent = accountSettingChannel.receiveAsFlow()

    fun loadingState() = viewModelScope.launch {
        accountSettingChannel.send(AccountSetting.Loading)
    }

    fun getAccessToken(): LiveData<String> = tokenPreferences.getAccessToken().asLiveData()

    sealed class AccountSetting {
        object Loading : AccountSetting()
    }
}