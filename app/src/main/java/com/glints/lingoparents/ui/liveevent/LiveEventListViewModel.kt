package com.glints.lingoparents.ui.liveevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.LiveEventListResponse
import com.glints.lingoparents.data.model.response.LiveEventSearchListResponse
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiveEventListViewModel() : ViewModel() {
    fun sendQueryToLiveEventListFragment(query: String) = viewModelScope.launch {
        EventBus.getDefault()
            .post(LiveEventListEvent.SendQueryToEventListFragment(query))
    }

    sealed class LiveEventListEvent {
        data class SendQueryToEventListFragment(val query: String) : LiveEventListEvent()
    }
}