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

class CompletedLiveEventViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    companion object {
        const val COMPLETED_TYPE = "completed"
    }

    private val completedLiveEventListChannel = Channel<CompletedLiveEventListEvent>()
    val completedLiveEventListEvent = completedLiveEventListChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        completedLiveEventListChannel.send(CompletedLiveEventListEvent.Loading)
    }

    private fun onApiCallSuccess(list: List<LiveEventListResponse.LiveEventItemResponse>) =
        viewModelScope.launch {
            completedLiveEventListChannel.send(CompletedLiveEventListEvent.Success(list))
        }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        completedLiveEventListChannel.send(CompletedLiveEventListEvent.Error(message))
    }

    fun onCompletedLiveEventItemClick(id: Int) = viewModelScope.launch {
        completedLiveEventListChannel.send(
            CompletedLiveEventListEvent.NavigateToDetailLiveEventFragment(
                id
            )
        )
    }

    fun loadCompletedLiveEventList() = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getLiveEventsByStatus(mapOf("status" to COMPLETED_TYPE))
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

    fun searchCompletedLiveEventList(title: String) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getCompletedLiveEventByStatusAndTitle(title)
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

    sealed class CompletedLiveEventListEvent {
        object Loading : CompletedLiveEventListEvent()
        data class Success(val list: List<LiveEventListResponse.LiveEventItemResponse>) :
            CompletedLiveEventListEvent()

        data class Error(val message: String) : CompletedLiveEventListEvent()
        data class NavigateToDetailLiveEventFragment(val id: Int) : CompletedLiveEventListEvent()
    }
}