package com.glints.lingoparents.ui.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.LiveEventDetailResponse
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailCourseViewModel(
    private val tokenPreferences: TokenPreferences,
    private val courseId: Int
) : ViewModel() {
    private val courseDetailEventChannel = Channel<CourseDetail>()
    val courseDetail = courseDetailEventChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        courseDetailEventChannel.send(CourseDetail.Loading)
    }

    //tambahin 1 var nama course
    private fun onApiCallSuccess(result: LiveEventDetailResponse.LiveEventDetailItemResponse) =
        viewModelScope.launch {
            courseDetailEventChannel.send(CourseDetail.Success(result))
        }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        courseDetailEventChannel.send(CourseDetail.Error(message))
    }

    fun getAccessToken(): LiveData<String> = tokenPreferences.getAccessToken().asLiveData()

    fun getCurrentCourseId(): Int = courseId

    fun getCourseDetailById(id: Int, accessToken: String) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
                //nama endpoint
            .getLiveEventById(id, accessToken)
            .enqueue(object : Callback<LiveEventDetailResponse> {
                override fun onResponse(
                    call: Call<LiveEventDetailResponse>,
                    response: Response<LiveEventDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()?.data!!
                        onApiCallSuccess(result)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<LiveEventDetailResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    sealed class CourseDetail {
        object Loading : CourseDetail()
        data class Success(val result: LiveEventDetailResponse.LiveEventDetailItemResponse) :
            CourseDetail()

        data class Error(val message: String) : CourseDetail()
    }
}