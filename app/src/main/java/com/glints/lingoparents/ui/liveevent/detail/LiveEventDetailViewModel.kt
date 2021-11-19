package com.glints.lingoparents.ui.liveevent.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.LiveEventDetailResponse
import com.glints.lingoparents.utils.ErrorUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiveEventDetailViewModel(
    state: SavedStateHandle
) : ViewModel() {

    val id = state.getLiveData("id", -1)

    private val liveEventDetailEventChannel = Channel<LiveEventDetailEvent>()
    val liveEventDetailEvent = liveEventDetailEventChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        liveEventDetailEventChannel.send(LiveEventDetailEvent.Loading)
    }

    private fun onApiCallSuccess(result: LiveEventDetailResponse.LiveEventDetailItemResponse) =
        viewModelScope.launch {
            liveEventDetailEventChannel.send(LiveEventDetailEvent.Success(result))
        }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        liveEventDetailEventChannel.send(LiveEventDetailEvent.Error(message))
    }

    fun getLiveEventDetailById(id: Int) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getLiveEventById(id)
            .enqueue(object : Callback<LiveEventDetailResponse> {
                override fun onResponse(
                    call: Call<LiveEventDetailResponse>,
                    response: Response<LiveEventDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()?.data!!
                        onApiCallSuccess(result)
                    }
                    else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<LiveEventDetailResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    sealed class LiveEventDetailEvent {
        object Loading : LiveEventDetailEvent()
        data class Success(val result: LiveEventDetailResponse.LiveEventDetailItemResponse) : LiveEventDetailEvent()
        data class Error(val message: String) : LiveEventDetailEvent()
    }
}