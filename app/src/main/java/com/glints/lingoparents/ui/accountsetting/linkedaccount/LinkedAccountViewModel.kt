package com.glints.lingoparents.ui.accountsetting.linkedaccount

import androidx.lifecycle.ViewModel
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

    private val searchChildrenChannel = Channel<SearchChildrenEvent>()
    val searchChildrenEvent = searchChildrenChannel.receiveAsFlow()

    private fun onApiCallSearchChildrenStarted() = viewModelScope.launch {
        searchChildrenChannel.send(SearchChildrenEvent.LoadingGetChildren)
    }

    private fun onApiCallSearchChildrenSuccess(result: ChildrenSearchResponse.ChildrenData) = viewModelScope.launch {
        searchChildrenChannel.send(SearchChildrenEvent.SuccessGetChildren(result))
    }

    private fun onApiCallSearchChildrenError(message: String) = viewModelScope.launch {
        searchChildrenChannel.send(SearchChildrenEvent.ErrorGetChildren(message))
    }

    private fun onApiCallInviteChildrenStarted() = viewModelScope.launch {
        searchChildrenChannel.send(SearchChildrenEvent.LoadingInvite)
    }

    private fun onApiCallInviteChildrenSuccess(result: InviteChildResponse.ChildrenData) = viewModelScope.launch {
        searchChildrenChannel.send(SearchChildrenEvent.SuccessInvite(result))
    }

    private fun onApiCallInviteChildrenError(message: String) = viewModelScope.launch {
        searchChildrenChannel.send(SearchChildrenEvent.ErrorInvite(message))
    }

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

    fun searchChildUsingStudentCode(parentId: Int, referralCode: String) = viewModelScope.launch {
        onApiCallSearchChildrenStarted()
        APIClient
            .service
            .searchChildUsingStudentCode(referralCode, parentId)
            .enqueue(object : Callback<ChildrenSearchResponse> {
                override fun onResponse(
                    call: Call<ChildrenSearchResponse>,
                    response: Response<ChildrenSearchResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallSearchChildrenSuccess(response.body()!!.data)
                    } else {
                        val apiError = ErrorUtils.parseErrorWithStatusAsString(response)
                        onApiCallSearchChildrenError(apiError.getMessage())
                    }
                }

                override fun onFailure(call: Call<ChildrenSearchResponse>, t: Throwable) {
                    onApiCallSearchChildrenError("Network Failed...")
                }
            })
    }

    fun inviteChild(parentId: Int, referralCode: String, parentRelationShip: String) = viewModelScope.launch {
        onApiCallInviteChildrenStarted()
        APIClient
            .service
            .inviteChild(parentId, referralCode, parentRelationShip)
            .enqueue(object : Callback<InviteChildResponse> {
                override fun onResponse(
                    call: Call<InviteChildResponse>,
                    response: Response<InviteChildResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallInviteChildrenSuccess(response.body()!!.data)
                    } else {
                        val apiError = ErrorUtils.parseErrorWithStatusAsString(response)
                        onApiCallInviteChildrenError(apiError.getMessage())
                    }
                }

                override fun onFailure(call: Call<InviteChildResponse>, t: Throwable) {
                    onApiCallInviteChildrenError("Network Failed...")
                }
            })
    }

    sealed class ParentCodeEvent {
        object Loading: ParentCodeEvent()
        data class Success(val data: String): ParentCodeEvent()
        data class Error(val message: String): ParentCodeEvent()
    }

    sealed class SearchChildrenEvent {
        object LoadingGetChildren: SearchChildrenEvent()
        object LoadingInvite: SearchChildrenEvent()
        data class SuccessGetChildren(val result: ChildrenSearchResponse.ChildrenData): SearchChildrenEvent()
        data class SuccessInvite(val result: InviteChildResponse.ChildrenData): SearchChildrenEvent()
        data class ErrorGetChildren(val message: String): SearchChildrenEvent()
        data class ErrorInvite(val message: String): SearchChildrenEvent()
    }
}