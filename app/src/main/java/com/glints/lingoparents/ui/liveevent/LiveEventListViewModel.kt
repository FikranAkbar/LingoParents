package com.glints.lingoparents.ui.liveevent

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.LiveEventListResponse
import com.glints.lingoparents.data.model.response.LiveEventSearchListResponse
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiveEventListViewModel(private val tokenPref: TokenPreferences) : ViewModel() {

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
            status.contains(TODAY_TYPE, true) -> {
                todayLiveEventListEventChannel.send(TodayLiveEventListEvent.Loading)
                Log.d("TEST", "Loading started")
            }
            status.contains(UPCOMING_TYPE, true) -> {
                upcomingLiveEventListChannel.send(UpcomingLiveEventListEvent.Loading)
            }
            status.contains(COMPLETED_TYPE, true) -> {
                completedLiveEventListChannel.send(CompletedLiveEventListEvent.Loading)
            }
        }
    }

    private fun onApiCallSuccess(
        status: String,
        list: List<LiveEventListResponse.LiveEventItemResponse>
    ) = viewModelScope.launch {
        when {
            status.contains(TODAY_TYPE, true) -> {
                todayLiveEventListEventChannel.send(TodayLiveEventListEvent.Success(list))
            }
            status.contains(UPCOMING_TYPE, true) -> {
                upcomingLiveEventListChannel.send(UpcomingLiveEventListEvent.Success(list))
            }
            status.contains(COMPLETED_TYPE, true) -> {
                completedLiveEventListChannel.send(CompletedLiveEventListEvent.Success(list))
            }
        }
    }

    private fun onApiCallError(status: String, message: String) = viewModelScope.launch {
        when {
            status.contains(TODAY_TYPE, true) -> {
                todayLiveEventListEventChannel.send(TodayLiveEventListEvent.Error(message))
            }
            status.contains(UPCOMING_TYPE, true) -> {
                upcomingLiveEventListChannel.send(UpcomingLiveEventListEvent.Error(message))
            }
            status.contains(COMPLETED_TYPE, true) -> {
                completedLiveEventListChannel.send(CompletedLiveEventListEvent.Error(message))
            }
        }
    }

    fun loadLiveEventList(status: String) = viewModelScope.launch {
        onApiCallStarted(status)
        APIClient
            .service
            .getLiveEventsByStatus(mapOf("status" to status))
            .enqueue(object : Callback<LiveEventListResponse> {
                override fun onResponse(
                    call: Call<LiveEventListResponse>,
                    response: Response<LiveEventListResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess(status, response.body()?.data!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(status, apiError.message())
                    }
                }

                override fun onFailure(call: Call<LiveEventListResponse>, t: Throwable) {
                    onApiCallError(status, "Network Failed...")
                }
            })
    }

    fun searchTodayLiveEventList(title: String) = viewModelScope.launch {
        onApiCallStarted(TODAY_TYPE)
        APIClient
            .service
            .getTodayLiveEventByStatusAndTitle(title)
            .enqueue(object : Callback<LiveEventSearchListResponse> {
                override fun onResponse(
                    call: Call<LiveEventSearchListResponse>,
                    response: Response<LiveEventSearchListResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess(TODAY_TYPE, response.body()?.data?.rows!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(TODAY_TYPE, apiError.message())
                    }
                }

                override fun onFailure(call: Call<LiveEventSearchListResponse>, t: Throwable) {
                    onApiCallError(TODAY_TYPE, "Network Failed...")
                }
            })
    }

    fun searchUpcomingLiveEventList(title: String) = viewModelScope.launch {
        onApiCallStarted(UPCOMING_TYPE)
        APIClient
            .service
            .getTodayLiveEventByStatusAndTitle(title)
            .enqueue(object : Callback<LiveEventSearchListResponse> {
                override fun onResponse(
                    call: Call<LiveEventSearchListResponse>,
                    response: Response<LiveEventSearchListResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess(UPCOMING_TYPE, response.body()?.data?.rows!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(UPCOMING_TYPE, apiError.message())
                    }
                }

                override fun onFailure(call: Call<LiveEventSearchListResponse>, t: Throwable) {
                    onApiCallError(UPCOMING_TYPE, "Network Failed...")
                }
            })
    }

    fun searchCompletedLiveEventList(title: String) = viewModelScope.launch {
        onApiCallStarted(COMPLETED_TYPE)
        APIClient
            .service
            .getTodayLiveEventByStatusAndTitle(title)
            .enqueue(object : Callback<LiveEventSearchListResponse> {
                override fun onResponse(
                    call: Call<LiveEventSearchListResponse>,
                    response: Response<LiveEventSearchListResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess(COMPLETED_TYPE, response.body()?.data?.rows!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(COMPLETED_TYPE, apiError.message())
                    }
                }

                override fun onFailure(call: Call<LiveEventSearchListResponse>, t: Throwable) {
                    onApiCallError(TODAY_TYPE, "Network Failed...")
                }
            })
    }

    fun getAccessToken(): LiveData<String> = tokenPref.getAccessToken().asLiveData()

    sealed class LiveEventListEvent {
        data class Loading(val title: String, val status: String) : LiveEventListEvent()
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