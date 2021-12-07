package com.glints.lingoparents.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.DataItem
import com.glints.lingoparents.data.model.response.StudentListResponse
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    //insight
    //live event
    //student list
    private val studentlistChannel = Channel<StudentList>()
    val studentList = studentlistChannel.receiveAsFlow()
    //code here

    private fun onApiCallStarted() = viewModelScope.launch {
        studentlistChannel.send(StudentList.Loading)
    }

    private fun onApiCallSuccess(list: List<DataItem>) =
        viewModelScope.launch {
            studentlistChannel.send(StudentList.Success(list))
        }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        studentlistChannel.send(StudentList.Error(message))
    }

    fun courseItemClick(id: Int) = viewModelScope.launch {
        studentlistChannel.send(StudentList.NavigateToDetailCourseFragment(id))
    }

    fun getAccessToken(): LiveData<String> = tokenPreferences.getAccessToken().asLiveData()
    fun getStudentList(id: Int, accessToken: String) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getStudentList(id, accessToken)
            .enqueue(object : Callback<StudentListResponse> {
                override fun onResponse(
                    call: Call<StudentListResponse>,
                    response: Response<StudentListResponse>
                ) {
                    if (response.isSuccessful) {
//                        onApiCallSuccess(response.body()?.data!!)
                        response.body()?.data?.let{
                            onApiCallSuccess(it)
                        }
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                        //onApiCallError("error")
                    }
                }

                override fun onFailure(call: Call<StudentListResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })

    }


    sealed class StudentList {
        object Loading : HomeViewModel.StudentList()
        data class Error(val message: String) : HomeViewModel.StudentList()
        data class Success(val list: List<DataItem>) :
            HomeViewModel.StudentList()
        //navigate to detail
        data class NavigateToDetailCourseFragment(val id: Int) :
            HomeViewModel.StudentList()

    }

}
