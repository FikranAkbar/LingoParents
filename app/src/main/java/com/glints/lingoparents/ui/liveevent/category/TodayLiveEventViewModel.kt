package com.glints.lingoparents.ui.liveevent.category

import androidx.lifecycle.ViewModel
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

class TodayLiveEventViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    companion object {
        const val TODAY_TYPE = "live"
    }

    private val todayLiveEventListEventChannel = Channel<TodayLiveEventListEvent>()
    val todayLiveEventListEvent = todayLiveEventListEventChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        todayLiveEventListEventChannel.send(TodayLiveEventListEvent.Loading)
    }

    private fun onApiCallSuccess(list: List<LiveEventListResponse.LiveEventItemResponse>) =
        viewModelScope.launch {
            todayLiveEventListEventChannel.send(TodayLiveEventListEvent.Success(list))
        }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        todayLiveEventListEventChannel.send(TodayLiveEventListEvent.Error(message))
    }

    fun onTodayLiveEventItemClick(id: Int) = viewModelScope.launch {
        todayLiveEventListEventChannel.send(
            TodayLiveEventListEvent.NavigateToDetailLiveEventFragment(
                id
            )
        )
    }

    fun loadTodayLiveEventList() = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getLiveEventsByStatus(mapOf("status" to TODAY_TYPE))
            .enqueue(object : Callback<LiveEventListResponse> {
                override fun onResponse(
                    call: Call<LiveEventListResponse>,
                    response: Response<LiveEventListResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess(response.body()?.data!!)
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

    fun searchTodayLiveEventList(title: String) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getTodayLiveEventByStatusAndTitle(title)
            .enqueue(object : Callback<LiveEventSearchListResponse> {
                override fun onResponse(
                    call: Call<LiveEventSearchListResponse>,
                    response: Response<LiveEventSearchListResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess(response.body()?.data?.rows!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<LiveEventSearchListResponse>, t: Throwable) {
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
}