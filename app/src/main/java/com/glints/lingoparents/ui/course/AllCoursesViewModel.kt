package com.glints.lingoparents.ui.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.AllCoursesResponse
import com.glints.lingoparents.ui.liveevent.LiveEventListViewModel
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllCoursesViewModel(private val tokenPreferences: TokenPreferences) : ViewModel() {
    private val allCoursesChannel = Channel<AllCoursesEvent>()
    val allCoursesEvent = allCoursesChannel.receiveAsFlow()

    ////////////tambahin navigate ke detail
    ///fun courseclick (untuk ke detail)
    private fun onApiCallStarted() = viewModelScope.launch {
        allCoursesChannel.send(AllCoursesEvent.Loading)
    }

    private fun onApiCallSuccess(list: List<AllCoursesResponse.CourseItemResponse>) =
        viewModelScope.launch {
            allCoursesChannel.send(AllCoursesEvent.Success(list))
        }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        allCoursesChannel.send(AllCoursesEvent.Error(message))
    }
    fun courseItemClick(id: Int) = viewModelScope.launch {
        allCoursesChannel.send(AllCoursesEvent.NavigateToDetailCourseFragment(id))
    }
    fun getAccessToken(): LiveData<String> = tokenPreferences.getAccessToken().asLiveData()
    fun getAllCourses(accessToken: String) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getCourseList(accessToken)
            .enqueue(object : Callback<AllCoursesResponse> {
                override fun onResponse(
                    call: Call<AllCoursesResponse>,
                    response: Response<AllCoursesResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess(response.body()?.data!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<AllCoursesResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })

    }

    //kode
    sealed class AllCoursesEvent {
        object Loading : AllCoursesEvent()
        data class Error(val message: String) : AllCoursesEvent()
        data class Success(val list: List<AllCoursesResponse.CourseItemResponse>) :
            AllCoursesViewModel.AllCoursesEvent()

        //navigate to detail
        data class NavigateToDetailCourseFragment(val id: Int) :
            AllCoursesViewModel.AllCoursesEvent()

    }
}