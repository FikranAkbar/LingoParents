package com.glints.lingoparents.ui.accountsetting.linkedaccount.codeinvitation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.ChildrenSearchResponse
import com.glints.lingoparents.data.model.response.InviteChildResponse
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChildrenCodeInvitationViewModel(private val tokenPreferences: TokenPreferences) :
    ViewModel() {
    private val searchChildrenCodeInvitationChannel = Channel<SearchChildrenCodeInvitationEvent>()
    val searchChildrenEvent = searchChildrenCodeInvitationChannel.receiveAsFlow()

    private fun onApiCallSearchChildrenByCodeStarted() = viewModelScope.launch {
        searchChildrenCodeInvitationChannel.send(SearchChildrenCodeInvitationEvent.LoadingGetChildren)
    }

    private fun onApiCallSearchChildrenByCodeSuccess(result: ChildrenSearchResponse.ChildrenData) =
        viewModelScope.launch {
            searchChildrenCodeInvitationChannel.send(SearchChildrenCodeInvitationEvent.SuccessGetChildren(
                result))
        }

    private fun onApiCallSearchChildrenByCodeError(message: String) = viewModelScope.launch {
        searchChildrenCodeInvitationChannel.send(SearchChildrenCodeInvitationEvent.ErrorGetChildren(
            message))
    }

    private fun onApiCallInviteChildrenSuccess(result: InviteChildResponse.ChildrenData) =
        viewModelScope.launch {
            searchChildrenCodeInvitationChannel.send(SearchChildrenCodeInvitationEvent.SuccessInvite(
                result))
        }

    private fun onApiCallInviteChildrenError(message: String) = viewModelScope.launch {
        searchChildrenCodeInvitationChannel.send(SearchChildrenCodeInvitationEvent.ErrorInvite(
            message))
    }

    fun searchChildUsingStudentCode(parentId: Int, referralCode: String) = viewModelScope.launch {
        onApiCallSearchChildrenByCodeStarted()
        APIClient
            .service
            .searchChildUsingStudentCode(referralCode, parentId)
            .enqueue(object : Callback<ChildrenSearchResponse> {
                override fun onResponse(
                    call: Call<ChildrenSearchResponse>,
                    response: Response<ChildrenSearchResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallSearchChildrenByCodeSuccess(response.body()!!.data)
                    } else {
                        val apiError = ErrorUtils.parseErrorWithStatusAsString(response)
                        onApiCallSearchChildrenByCodeError(apiError.getMessage())
                    }
                }

                override fun onFailure(call: Call<ChildrenSearchResponse>, t: Throwable) {
                    onApiCallSearchChildrenByCodeError("Network Failed...")
                }
            })
    }

    fun inviteChild(parentId: Int, referralCode: String, parentRelationShip: String) =
        viewModelScope.launch {
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

    sealed class SearchChildrenCodeInvitationEvent {
        object LoadingGetChildren : SearchChildrenCodeInvitationEvent()
        data class SuccessGetChildren(val result: ChildrenSearchResponse.ChildrenData) :
            SearchChildrenCodeInvitationEvent()

        data class SuccessInvite(val result: InviteChildResponse.ChildrenData) :
            SearchChildrenCodeInvitationEvent()

        data class ErrorGetChildren(val message: String) : SearchChildrenCodeInvitationEvent()
        data class ErrorInvite(val message: String) : SearchChildrenCodeInvitationEvent()
    }

    fun getParentId() = tokenPreferences.getUserId().asLiveData()
}