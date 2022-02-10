package com.glints.lingoparents.ui.accountsetting.linkedaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.api.APIError
import com.glints.lingoparents.data.model.response.*
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LinkedAccountViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    private val parentCodeChannel = Channel<ParentCodeEvent>()
    val parentCodeEvent = parentCodeChannel.receiveAsFlow()

    private fun onApiCallGetParentCodeStarted() = viewModelScope.launch {
        parentCodeChannel.send(ParentCodeEvent.Loading)
    }

    private fun onApiCallGetParentCodeSuccess(data: String) = viewModelScope.launch {
        parentCodeChannel.send(ParentCodeEvent.Success(data))
    }

    private fun onApiCallGetParentCodeError(message: String) = viewModelScope.launch {
        parentCodeChannel.send(ParentCodeEvent.Error(message))
    }

    fun getParentId() = tokenPreferences.getUserId().asLiveData()

    fun getParentCode(parentId: Int) = viewModelScope.launch {
        onApiCallGetParentCodeStarted()
        APIClient
            .service
            .getParentCode(parentId)
            .enqueue(object : Callback<ShowParentCodeResponse> {
                override fun onResponse(
                    call: Call<ShowParentCodeResponse>,
                    response: Response<ShowParentCodeResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallGetParentCodeSuccess(response.body()!!.data)
                    } else {
                        val apiError = ErrorUtils.parseErrorWithStatusAsString(response)
                        onApiCallGetParentCodeError(apiError.getMessage())
                    }
                }

                override fun onFailure(call: Call<ShowParentCodeResponse>, t: Throwable) {
                    onApiCallGetParentCodeError("Network Failed...")
                }
            })
    }

    sealed class ParentCodeEvent {
        object Loading: ParentCodeEvent()
        data class Success(val data: String): ParentCodeEvent()
        data class Error(val message: String): ParentCodeEvent()
    }
}