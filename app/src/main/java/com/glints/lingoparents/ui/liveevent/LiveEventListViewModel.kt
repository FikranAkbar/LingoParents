package com.glints.lingoparents.ui.liveevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.model.response.LiveEventListResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LiveEventListViewModel : ViewModel() {
    private val liveEventListEventChannel = Channel<LiveEventListEvent>()
    val liveEventListEvent = liveEventListEventChannel.receiveAsFlow()

    private val todayLiveEventListEventChannel = Channel<TodayLiveEventListEvent>()
    val todayLiveEventListEvent = todayLiveEventListEventChannel.receiveAsFlow()

    private val upcomingLiveEventListChannel = Channel<UpcomingLiveEventListEvent>()
    val upcomingLiveEventListEvent = upcomingLiveEventListChannel.receiveAsFlow()

    private val completedLiveEventListChannel = Channel<CompletedLiveEventListEvent>()
    val completedLiveEventListEvent = completedLiveEventListChannel.receiveAsFlow()

    fun onLiveEventItemClick(id: Int) = viewModelScope.launch {
        liveEventListEventChannel.send(LiveEventListEvent.NavigateToDetailLiveEventFragment(id))
    }

    private fun onApiCallStarted(status: String) = viewModelScope.launch {
        when {
            status.contains("live", true) -> {
                todayLiveEventListEventChannel.send(TodayLiveEventListEvent.Loading)
            }
            status.contains("upcoming", true) -> {
                upcomingLiveEventListChannel.send(UpcomingLiveEventListEvent.Loading)
            }
            status.contains("completed", true) -> {
                completedLiveEventListChannel.send(CompletedLiveEventListEvent.Loading)
            }
        }
    }

    private fun onApiCallSuccess(
        message: String,
        list: List<LiveEventListResponse.LiveEventItemResponse>
    ) = viewModelScope.launch {
        when {
            message.contains("live", true) -> {
                todayLiveEventListEventChannel.send(TodayLiveEventListEvent.Success(list))
            }
            message.contains("upcoming", true) -> {
                upcomingLiveEventListChannel.send(UpcomingLiveEventListEvent.Success(list))
            }
            message.contains("completed", true) -> {
                completedLiveEventListChannel.send(CompletedLiveEventListEvent.Success(list))
            }
        }
    }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        when {
            message.contains("live", true) -> {
                todayLiveEventListEventChannel.send(TodayLiveEventListEvent.Error("error message"))
            }
            message.contains("upcoming", true) -> {
                upcomingLiveEventListChannel.send(UpcomingLiveEventListEvent.Error("error message"))
            }
            message.contains("completed", true) -> {
                completedLiveEventListChannel.send(CompletedLiveEventListEvent.Error("error message"))
            }
        }
    }

    fun loadTodayLiveEventList(status: String) = viewModelScope.launch {

    }

    sealed class LiveEventListEvent {
        data class NavigateToDetailLiveEventFragment(val id: Int) : LiveEventListEvent()
    }

    sealed class TodayLiveEventListEvent {
        object Loading : TodayLiveEventListEvent()
        data class Success(val list: List<LiveEventListResponse.LiveEventItemResponse>) :
            TodayLiveEventListEvent()

        data class Error(val message: String) : TodayLiveEventListEvent()
    }

    sealed class UpcomingLiveEventListEvent {
        object Loading : UpcomingLiveEventListEvent()
        data class Success(val list: List<LiveEventListResponse.LiveEventItemResponse>) :
            UpcomingLiveEventListEvent()

        data class Error(val message: String) : UpcomingLiveEventListEvent()
    }

    sealed class CompletedLiveEventListEvent {
        object Loading : CompletedLiveEventListEvent()
        data class Success(val list: List<LiveEventListResponse.LiveEventItemResponse>) :
            CompletedLiveEventListEvent()

        data class Error(val message: String) : CompletedLiveEventListEvent()
    }
}