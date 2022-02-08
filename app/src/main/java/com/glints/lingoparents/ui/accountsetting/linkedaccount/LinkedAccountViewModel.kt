package com.glints.lingoparents.ui.accountsetting.linkedaccount

import androidx.lifecycle.ViewModel
import com.glints.lingoparents.data.model.response.ChildrenSearchResponse
import com.glints.lingoparents.data.model.response.InviteChildResponse
import com.glints.lingoparents.data.model.response.LinkedAccountsResponse
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class LinkedAccountViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    companion object {
        const val ACCEPT_ACTION = "accept"
        const val DECLINE_ACTION = "decline"
        const val CANCEL_ACTION = "cancel"
    }

    private val linkedAccountListChannel = Channel<LinkedAccountListEvent>()
    val linkedAccountListEvent = linkedAccountListChannel.receiveAsFlow()

    private val parentCodeChannel = Channel<ParentCodeEvent>()
    val parentCodeEvent = parentCodeChannel.receiveAsFlow()

    private val searchChildrenChannel = Channel<SearchChildrenEvent>()
    val searchChildrenEvent = searchChildrenChannel.receiveAsFlow()

    sealed class LinkedAccountListEvent {
        object LoadingGetList: LinkedAccountListEvent()
        object LoadingAction: LinkedAccountListEvent()
        data class SuccessGetList(val result: LinkedAccountsResponse.ChildrenData): LinkedAccountListEvent()
        object SuccessAction: LinkedAccountListEvent()
        data class ErrorGetList(val message: String): LinkedAccountListEvent()
        data class ErrorAction(val message: String): LinkedAccountListEvent()
    }

    sealed class ParentCodeEvent {
        object Loading: ParentCodeEvent()
        data class Success(val result: String): ParentCodeEvent()
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