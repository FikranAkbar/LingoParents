package com.glints.lingoparents.ui.liveevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.LiveEventListResponse
import com.glints.lingoparents.utils.ErrorUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiveEventListViewModel : ViewModel() {

    companion object {
        const val TODAY_TYPE = "live"
        const val UPCOMING_TYPE = "upcoming"
        const val COMPLETED_TYPE = "completed"
    }

    private val todayLiveEventListEventChannel = Channel<TodayLiveEventListEvent>()
    val todayLiveEventListEvent = todayLiveEventListEventChannel.receiveAsFlow()

    private val upcomingLiveEventListChannel = Channel<UpcomingLiveEventListEvent>()
    val upcomingLiveEventListEvent = upcomingLiveEventListChannel.receiveAsFlow()

    private val completedLiveEventListChannel = Channel<CompletedLiveEventListEvent>()
    val completedLiveEventListEvent = completedLiveEventListChannel.receiveAsFlow()

    fun onTodayLiveEventItemClick(id: Int) = viewModelScope.launch {
        todayLiveEventListEventChannel.send(TodayLiveEventListEvent.NavigateToDetailLiveEventFragment(id))
    }

    fun onUpcomingLiveEventItemClick(id: Int) = viewModelScope.launch {
        upcomingLiveEventListChannel.send(UpcomingLiveEventListEvent.NavigateToDetailLiveEventFragment(id))
    }

    fun onCompletedLiveEventItemClick(id: Int) = viewModelScope.launch {
        completedLiveEventListChannel.send(CompletedLiveEventListEvent.NavigateToDetailLiveEventFragment(id))
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
        status: String,
        list: List<LiveEventListResponse.LiveEventItemResponse>
    ) = viewModelScope.launch {
        when {
            status.contains("live", true) -> {
                todayLiveEventListEventChannel.send(TodayLiveEventListEvent.Success(list))
            }
            status.contains("upcoming", true) -> {
                upcomingLiveEventListChannel.send(UpcomingLiveEventListEvent.Success(list))
            }
            status.contains("completed", true) -> {
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
        onApiCallStarted(status)
        APIClient
            .service
            .getLiveEventsByStatus(status)
            .enqueue(object : Callback<LiveEventListResponse> {
                override fun onResponse(
                    call: Call<LiveEventListResponse>,
                    response: Response<LiveEventListResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess(status, response.body()?.data!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<LiveEventListResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    sealed class TodayLiveEventListEvent {
        object Loading : TodayLiveEventListEvent()
        data class Success(val list: List<LiveEventListResponse.LiveEventItemResponse>) :
            TodayLiveEventListEvent()

        data class Error(val message: String) : TodayLiveEventListEvent()
        data class NavigateToDetailLiveEventFragment(val id: Int) : TodayLiveEventListEvent()
    }

    sealed class UpcomingLiveEventListEvent {
        object Loading : UpcomingLiveEventListEvent()
        data class Success(val list: List<LiveEventListResponse.LiveEventItemResponse>) :
            UpcomingLiveEventListEvent()

        data class Error(val message: String) : UpcomingLiveEventListEvent()
        data class NavigateToDetailLiveEventFragment(val id: Int) : UpcomingLiveEventListEvent()
    }

    sealed class CompletedLiveEventListEvent {
        object Loading : CompletedLiveEventListEvent()
        data class Success(val list: List<LiveEventListResponse.LiveEventItemResponse>) :
            CompletedLiveEventListEvent()

        data class Error(val message: String) : CompletedLiveEventListEvent()
        data class NavigateToDetailLiveEventFragment(val id: Int) : CompletedLiveEventListEvent()
    }
}