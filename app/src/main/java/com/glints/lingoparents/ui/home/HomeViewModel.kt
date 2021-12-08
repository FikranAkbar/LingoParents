package com.glints.lingoparents.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.*
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    companion object {
        const val INSIGHT_TYPE = "recentInsight"
        const val EVENT_TYPE = "allEvent"
        const val STUDENTLIST_TYPE = "studentList"
    }

    //insight
    //live event
    //student list
    private val recentInsightChannel = Channel<RecentInsight>()
    val recentInsight = recentInsightChannel.receiveAsFlow()

    private val allEventChannel = Channel<AllEvent>()
    val allEvent = allEventChannel.receiveAsFlow()

    private val studentlistChannel = Channel<StudentList>()
    val studentList = studentlistChannel.receiveAsFlow()

    //    private fun onApiCallStarted() = viewModelScope.launch {
//        studentlistChannel.send(StudentList.Loading)
//    }
    private fun onApiCallStarted(status: String) = viewModelScope.launch {
        when {
            status.contains(INSIGHT_TYPE, true) -> {
                recentInsightChannel.send(RecentInsight.Loading)
            }
            //event
            status.contains(EVENT_TYPE, true) -> {
                allEventChannel.send(AllEvent.Loading)
            }
            status.contains(STUDENTLIST_TYPE, true) -> {
                studentlistChannel.send(StudentList.Loading)
            }
        }
    }

    //    private fun onApiCallSuccess(list: List<DataItem>) =
//        viewModelScope.launch {
//            studentlistChannel.send(StudentList.Success(list))
//        } original
    private fun onRecentInsightApiCallSuccess(list: MutableList<RecentInsightItem>) =
        viewModelScope.launch {
            recentInsightChannel.send(RecentInsight.Success(list))
        }

    //ini event
    private fun onAllEventApiCallSuccess(list: MutableList<AllEventItem>) =
        viewModelScope.launch {
            allEventChannel.send(AllEvent.Success(list))
        }

    private fun onStudentListApiCallSuccess(list: List<DataItem>) =
        viewModelScope.launch {
            studentlistChannel.send(StudentList.Success(list))
        }

    //original
//    private fun onApiCallError(message: String) = viewModelScope.launch {
//        studentlistChannel.send(StudentList.Error(message))
//    }

    private fun onApiCallError(status: String, message: String) = viewModelScope.launch {
        //studentlistChannel.send(StudentList.Error(message))
        when {
            status.contains(INSIGHT_TYPE, true) -> {
                recentInsightChannel.send(RecentInsight.Error(message))
            }
            //event
            status.contains(EVENT_TYPE, true) -> {
                allEventChannel.send(AllEvent.Loading)
            }
            status.contains(STUDENTLIST_TYPE, true) -> {
                studentlistChannel.send(StudentList.Error(message))
            }
        }
    }

    //insight itemclick
    //event itemclick
    fun studentItemClick(id: Int) = viewModelScope.launch {
        studentlistChannel.send(StudentList.NavigateToProgressProfileFragment(id))
    }

    fun getAccessToken(): LiveData<String> = tokenPreferences.getAccessToken().asLiveData()
    fun getAccessParentId(): LiveData<Int> = tokenPreferences.getAccessParentId().asLiveData()

    fun getRecentInsight(status: String) = viewModelScope.launch {
        onApiCallStarted(status)
        APIClient
            .service
            .getRecentInsight()
            .enqueue(object : Callback<RecentInsightResponse> {
                override fun onResponse(
                    call: Call<RecentInsightResponse>,
                    response: Response<RecentInsightResponse>
                ) {
                    if (response.isSuccessful) {
//                        onRecentInsightApiCallSuccess(response.body()?.)
                        response.body()?.message?.let {
                            onRecentInsightApiCallSuccess(it)
                        }
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(status, apiError.message())
                        //onApiCallError("error")
                    }
                }

                override fun onFailure(call: Call<RecentInsightResponse>, t: Throwable) {
                    onApiCallError(status, "Network Failed...")
                }
            })

    }

    //event
    fun getAllEvent(status: String) = viewModelScope.launch {
        onApiCallStarted(status)
        APIClient
            .service
            .getAllEvent()
            .enqueue(object : Callback<AllEventResponse> {
                override fun onResponse(
                    call: Call<AllEventResponse>,
                    response: Response<AllEventResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.data?.let {
                            onAllEventApiCallSuccess(it)
                        }
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(status, apiError.message())
                    }
                }

                override fun onFailure(call: Call<AllEventResponse>, t: Throwable) {
                    onApiCallError(status, "Network Failed...")
                }
            })

    }

    fun getStudentList(status: String, id: Int) = viewModelScope.launch {
        onApiCallStarted(status)
        APIClient
            .service
            .getStudentList(id)
            .enqueue(object : Callback<StudentListResponse> {
                override fun onResponse(
                    call: Call<StudentListResponse>,
                    response: Response<StudentListResponse>
                ) {
                    if (response.isSuccessful) {
//                        onApiCallSuccess(response.body()?.data!!)
                        response.body()?.data?.let {
                            onStudentListApiCallSuccess(it)
                        }
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(status, apiError.message())
                    }
                }

                override fun onFailure(call: Call<StudentListResponse>, t: Throwable) {
                    onApiCallError(status, "Network Failed...")
                }
            })

    }

    sealed class RecentInsight {
        object Loading : HomeViewModel.RecentInsight()
        data class Error(val message: String) : HomeViewModel.RecentInsight()
        data class Success(val list: MutableList<RecentInsightItem>) :
            HomeViewModel.RecentInsight()
    }

    sealed class AllEvent {
        object Loading : HomeViewModel.AllEvent()
        data class Error(val message: String) : HomeViewModel.AllEvent()
        data class Success(val list: MutableList<AllEventItem>) :
            HomeViewModel.AllEvent()
    }

    sealed class StudentList {
        object Loading : HomeViewModel.StudentList()
        data class Error(val message: String) : HomeViewModel.StudentList()
        data class Success(val list: List<DataItem>) :
            HomeViewModel.StudentList()

        //navigate to progress
        data class NavigateToProgressProfileFragment(val id: Int) :
            HomeViewModel.StudentList()

    }

}
