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
    companion object {
        const val ACCEPT_ACTION = "accept"
        const val DECLINE_ACTION = "decline"
        const val CANCEL_ACTION = "cancel"
    }

    private val linkedAccountListChannel = Channel<LinkedAccountListEvent>()
    val linkedAccountListEvent = linkedAccountListChannel.receiveAsFlow()

    private fun onApiCallGetListOfLinkedAccountStarted() = viewModelScope.launch {
        linkedAccountListChannel.send(LinkedAccountListEvent.LoadingGetList)
    }

    private fun onApiCallGetListOfLinkedAccountSuccess(result: List<LinkedAccountsResponse.ChildrenData>) = viewModelScope.launch {
        linkedAccountListChannel.send(LinkedAccountListEvent.SuccessGetList(result))
    }

    private fun onApiCallGetListOfLinkedAccountError(message: String) = viewModelScope.launch {
        linkedAccountListChannel.send(LinkedAccountListEvent.ErrorGetList(message))
    }

    private fun onApiCallDoActionWithLinkingAccountStarted() = viewModelScope.launch {
        linkedAccountListChannel.send(LinkedAccountListEvent.LoadingAction)
    }

    private fun onApiCallDoActionWithLinkingAccountSuccess(result: LinkingAccountActionResponse.ChildrenData) = viewModelScope.launch {
        linkedAccountListChannel.send(LinkedAccountListEvent.SuccessAction(result))
    }

    private fun onApiCallDoActionWithLinkingAccountError(message: String) = viewModelScope.launch {
        linkedAccountListChannel.send(LinkedAccountListEvent.ErrorAction(message))
    }

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

    fun getListOfRequestedLinkedAccount(parentId: Int) = viewModelScope.launch {
        onApiCallGetListOfLinkedAccountStarted()
        APIClient
            .service
            .getListOfLinkedAccount(parentId, mapOf("idCreated" to parentId.toString()))
            .enqueue(object : Callback<LinkedAccountsResponse> {
                override fun onResponse(
                    call: Call<LinkedAccountsResponse>,
                    response: Response<LinkedAccountsResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallGetListOfLinkedAccountSuccess(response.body()!!.data)
                    } else {
                        val apiError = ErrorUtils.parseErrorWithStatusAsString(response)
                        onApiCallGetListOfLinkedAccountError(apiError.getMessage())
                    }
                }

                override fun onFailure(call: Call<LinkedAccountsResponse>, t: Throwable) {
                    onApiCallGetListOfLinkedAccountError("Network Failed...")
                }
            })
    }

    fun getListOfInvitedLinkedAccount(parentId: Int) = viewModelScope.launch {
        onApiCallGetListOfLinkedAccountStarted()
        APIClient
            .service
            .getListOfLinkedAccount(parentId)
            .enqueue(object : Callback<LinkedAccountsResponse> {
                override fun onResponse(
                    call: Call<LinkedAccountsResponse>,
                    response: Response<LinkedAccountsResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallGetListOfLinkedAccountSuccess(response.body()!!.data)
                    } else {
                        val apiError = ErrorUtils.parseErrorWithStatusAsString(response)
                        onApiCallGetListOfLinkedAccountError(apiError.getMessage())
                    }
                }

                override fun onFailure(call: Call<LinkedAccountsResponse>, t: Throwable) {
                    onApiCallGetListOfLinkedAccountError("Network Failed...")
                }
            })
    }

    fun doActionWithLinkingAccount(parentId: Int, studentId: Int, actionType: String) = viewModelScope.launch {
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
        object LoadingGetList: LinkedAccountListEvent()
        object LoadingAction: LinkedAccountListEvent()
        data class SuccessGetList(val result: List<LinkedAccountsResponse.ChildrenData>): LinkedAccountListEvent()
        data class SuccessAction(val result: LinkingAccountActionResponse.ChildrenData): LinkedAccountListEvent()
        data class ErrorGetList(val message: String): LinkedAccountListEvent()
        data class ErrorAction(val message: String): LinkedAccountListEvent()
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