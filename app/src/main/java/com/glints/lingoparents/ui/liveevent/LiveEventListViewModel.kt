package com.glints.lingoparents.ui.liveevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.ui.dashboard.DashboardViewModel.DashboardEvent
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class LiveEventListViewModel : ViewModel() {

    fun sendBlankQueryToLiveEventListFragment() = viewModelScope.launch {
        EventBus.getDefault()
            .post(LiveEventListEvent.SendBlankQueryToEventList)
    }

    fun sendQueryToLiveEventListFragment(query: String) = viewModelScope.launch {
        EventBus.getDefault()
            .post(LiveEventListEvent.SendQueryToEventList(query))
    }

    sealed class LiveEventListEvent {
        object SendBlankQueryToEventList : LiveEventListEvent()
        data class SendQueryToEventList(val query: String) : LiveEventListEvent()
    }
}