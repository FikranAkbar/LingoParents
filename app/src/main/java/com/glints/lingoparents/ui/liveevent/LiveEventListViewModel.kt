package com.glints.lingoparents.ui.liveevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.ui.dashboard.DashboardViewModel.DashboardEvent
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class LiveEventListViewModel : ViewModel() {

    fun sendBlankQueryToLiveEventListFragment() = viewModelScope.launch {
        EventBus.getDefault()
            .post(LiveEventListEvent.SendBlankQueryToTodayEventList)
        EventBus.getDefault()
            .postSticky(LiveEventListEvent.SendBlankQueryToUpcomingEventList)
        EventBus.getDefault()
            .postSticky(LiveEventListEvent.SendBlankQueryToCompletedEventList)
    }

    fun sendQueryToLiveEventListFragment(query: String) = viewModelScope.launch {
        EventBus.getDefault()
            .post(LiveEventListEvent.SendQueryToTodayEventList(query))
        EventBus.getDefault()
            .postSticky(LiveEventListEvent.SendQueryToUpcomingEventList(query))
        EventBus.getDefault()
            .postSticky(LiveEventListEvent.SendQueryToCompletedEventList(query))
    }

    sealed class LiveEventListEvent {
        object SendBlankQueryToTodayEventList : LiveEventListEvent()
        object SendBlankQueryToUpcomingEventList : LiveEventListEvent()
        object SendBlankQueryToCompletedEventList : LiveEventListEvent()
        data class SendQueryToTodayEventList(val query: String) : LiveEventListEvent()
        data class SendQueryToUpcomingEventList(val query: String) : LiveEventListEvent()
        data class SendQueryToCompletedEventList(val query: String) : LiveEventListEvent()
    }
}