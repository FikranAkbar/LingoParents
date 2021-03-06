package com.glints.lingoparents.ui.progress

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.StudentListResponse
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProgressViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    private var studentId = 0

    private val progressEventChannel = Channel<ProgressEvent>()
    val progressEvent = progressEventChannel.receiveAsFlow()

    var lastSelectedSpinnerItem = 0

    private fun onApiCallStarted() = viewModelScope.launch {
        progressEventChannel.send(ProgressEvent.Loading)
    }

    private fun onApiCallSuccess(result: List<StudentListResponse.DataItem>) =
        viewModelScope.launch {
            progressEventChannel.send(ProgressEvent.Success(result))
        }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        progressEventChannel.send(ProgressEvent.Error(message))
    }

    fun sendEventToProfileFragment(event: EventBusActionToStudentProfile) = viewModelScope.launch {
        EventBus.getDefault().post(event)
    }

    fun sendStickyEventToLearningProgressFragment(event: EventBusActionToStudentLearningProgress) = viewModelScope.launch {
        EventBus.getDefault().postSticky(event)
    }

    fun getParentId() = tokenPreferences.getUserId().asLiveData()

    fun getStudentListByParentId(id: String) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getStudentListByParentId(id)
            .enqueue(object : Callback<StudentListResponse> {
                override fun onResponse(
                    call: Call<StudentListResponse>,
                    response: Response<StudentListResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.data != null) {
                            val list = response.body()?.data!!
                            onApiCallSuccess(list)
                            println("STUDENT DATA: $list")
                        }
                        else {
                            onApiCallError(response.body()?.message!!)
                        }
                    }
                    else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<StudentListResponse>, t: Throwable) {
                    onApiCallError("Network failed...")
                }
            })
    }

    fun makeMapFromStudentList(list: List<StudentListResponse.DataItem>) =
        viewModelScope.launch {
            Log.d("studentList", list.toString())
            val nameList = mutableMapOf<String, Int>()
            for (item in list) {
                nameList[item.name as String] = item.student_id as Int
            }

            progressEventChannel.send(ProgressEvent.NameListGenerated(nameList))
        }

    fun saveSelectedStudentId(newId: Int) = viewModelScope.launch {
        studentId = newId
    }

    sealed class ProgressEvent {
        object Loading : ProgressEvent()
        data class Success(val result: List<StudentListResponse.DataItem>) : ProgressEvent()
        data class Error(val message: String) : ProgressEvent()
        data class NameListGenerated(val result: Map<String, Int>) : ProgressEvent()
    }

    sealed class EventBusActionToStudentProfile {
        data class SendStudentId(val studentId: Int) : EventBusActionToStudentProfile()
    }

    sealed class EventBusActionToStudentLearningProgress {
        data class SendStudentId(val studentId: Int) : EventBusActionToStudentLearningProgress()
        object RequestStudentId : EventBusActionToStudentLearningProgress()
    }
}