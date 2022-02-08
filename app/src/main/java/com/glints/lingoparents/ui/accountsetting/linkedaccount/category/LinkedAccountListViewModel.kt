package com.glints.lingoparents.ui.accountsetting.linkedaccount.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.LinkedAccountsResponse
import com.glints.lingoparents.data.model.response.LinkingAccountActionResponse
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LinkedAccountListViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    companion object {
        const val ACCEPT_ACTION = "accept"
        const val DECLINE_ACTION = "decline"
        const val CANCEL_ACTION = "cancel"
    }

    var parentId = ""

    private val linkedAccountListChannel = Channel<LinkedAccountListEvent>()
    val linkedAccountListEvent = linkedAccountListChannel.receiveAsFlow()

    private fun onApiCallGetListOfInvitedLinkedAccountStarted() = viewModelScope.launch {
        linkedAccountListChannel.send(LinkedAccountListEvent.LoadingGetInvitedList)
    }

    private fun onApiCallGetListOfRequestedLinkedAccountStarted() = viewModelScope.launch {
        linkedAccountListChannel.send(LinkedAccountListEvent.LoadingGetRequestedList)
    }

    private fun onApiCallGetListOfInvitedLinkedAccountSuccess(result: List<LinkedAccountsResponse.ChildrenData>) =
        viewModelScope.launch {
            linkedAccountListChannel.send(LinkedAccountListEvent.SuccessGetInvitedList(result))
        }

    private fun onApiCallGetListOfRequestedLinkedAccountSuccess(result: List<LinkedAccountsResponse.ChildrenData>) =
        viewModelScope.launch {
            linkedAccountListChannel.send(LinkedAccountListEvent.SuccessGetRequestedList(result))
        }

    private fun onApiCallGetListOfInvitedLinkedAccountError(message: String) = viewModelScope.launch {
        linkedAccountListChannel.send(LinkedAccountListEvent.ErrorGetInvitedList(message))
    }

    private fun onApiCallGetListOfRequestedLinkedAccountError(message: String) = viewModelScope.launch {
        linkedAccountListChannel.send(LinkedAccountListEvent.ErrorGetRequestedList(message))
    }

    private fun onApiCallDoActionWithLinkingAccountStarted() = viewModelScope.launch {
        linkedAccountListChannel.send(LinkedAccountListEvent.LoadingAction)
    }

    private fun onApiCallDoActionWithLinkingAccountSuccess(result: LinkingAccountActionResponse.ChildrenData) =
        viewModelScope.launch {
            linkedAccountListChannel.send(LinkedAccountListEvent.SuccessAction(result))
        }

    private fun onApiCallDoActionWithLinkingAccountError(message: String) = viewModelScope.launch {
        linkedAccountListChannel.send(LinkedAccountListEvent.ErrorAction(message))
    }

    fun getParentId(): LiveData<String> = tokenPreferences.getUserId().asLiveData()

    fun getListOfRequestedLinkedAccount(parentId: String) = viewModelScope.launch {
        onApiCallGetListOfRequestedLinkedAccountStarted()
        APIClient
            .service
            .getListOfRequestedLinkedAccount(parentId)
            .enqueue(object : Callback<LinkedAccountsResponse> {
                override fun onResponse(
                    call: Call<LinkedAccountsResponse>,
                    response: Response<LinkedAccountsResponse>,
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()!!.data

                        if (result != null) {
                            onApiCallGetListOfInvitedLinkedAccountSuccess(result)
                        } else {
                            onApiCallGetListOfInvitedLinkedAccountError(response.body()!!.message)
                        }

                    } else {
                        val apiError = ErrorUtils.parseErrorWithStatusAsString(response)
                        onApiCallGetListOfInvitedLinkedAccountError(apiError.getMessage())
                    }
                }

                override fun onFailure(call: Call<LinkedAccountsResponse>, t: Throwable) {
                    onApiCallGetListOfRequestedLinkedAccountError("Network Failed...")
                }
            })
    }

    fun getListOfInvitedLinkedAccount(parentId: String) = viewModelScope.launch {
        onApiCallGetListOfInvitedLinkedAccountStarted()
        APIClient
            .service
            .getListOfInvitedLinkedAccount(parentId, mapOf("idCreated" to parentId))
            .enqueue(object : Callback<LinkedAccountsResponse> {
                override fun onResponse(
                    call: Call<LinkedAccountsResponse>,
                    response: Response<LinkedAccountsResponse>,
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()!!.data

                        if (result != null) {
                            onApiCallGetListOfInvitedLinkedAccountSuccess(result)
                        } else {
                            onApiCallGetListOfInvitedLinkedAccountError(response.body()!!.message)
                        }

                    } else {
                        val apiError = ErrorUtils.parseErrorWithStatusAsString(response)
                        onApiCallGetListOfInvitedLinkedAccountError(apiError.getMessage())
                    }
                }

                override fun onFailure(call: Call<LinkedAccountsResponse>, t: Throwable) {
                    onApiCallGetListOfInvitedLinkedAccountError("Network Failed...")
                }
            })
    }

    fun doActionWithLinkingAccount(parentId: Int, studentId: Int, actionType: String) =
        viewModelScope.launch {
            onApiCallDoActionWithLinkingAccountStarted()
            APIClient
                .service
                .doActionWithLinkingAccount(parentId, studentId, mapOf("button" to actionType))
                .enqueue(object : Callback<LinkingAccountActionResponse> {
                    override fun onResponse(
                        call: Call<LinkingAccountActionResponse>,
                        response: Response<LinkingAccountActionResponse>,
                    ) {
                        if (response.isSuccessful) {
                            onApiCallDoActionWithLinkingAccountSuccess(response.body()!!.data)
                        } else {
                            val apiError = ErrorUtils.parseErrorWithStatusAsString(response)
                            onApiCallDoActionWithLinkingAccountError(apiError.getMessage())
                        }
                    }

                    override fun onFailure(call: Call<LinkingAccountActionResponse>, t: Throwable) {
                        onApiCallDoActionWithLinkingAccountError("Network Failed...")
                    }
                })
        }

    sealed class LinkedAccountListEvent {
        object LoadingGetInvitedList : LinkedAccountListEvent()
        object LoadingGetRequestedList : LinkedAccountListEvent()
        object LoadingAction : LinkedAccountListEvent()
        data class SuccessGetInvitedList(val result: List<LinkedAccountsResponse.ChildrenData>) :
            LinkedAccountListEvent()

        data class SuccessGetRequestedList(val result: List<LinkedAccountsResponse.ChildrenData>) :
            LinkedAccountListEvent()

        data class SuccessAction(val result: LinkingAccountActionResponse.ChildrenData) :
            LinkedAccountListEvent()

        data class ErrorGetInvitedList(val message: String) : LinkedAccountListEvent()
        data class ErrorGetRequestedList(val message: String) : LinkedAccountListEvent()
        data class ErrorAction(val message: String) : LinkedAccountListEvent()
    }
}