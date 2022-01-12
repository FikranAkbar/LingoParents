package com.glints.lingoparents.ui.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.DetailCourseResponse
import com.glints.lingoparents.data.model.response.TrxCourseCardsItem
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

    private fun onApiCallSuccess(courseTitle: String, result: List<TrxCourseCardsItem>) =
        viewModelScope.launch {
            courseDetailEventChannel.send(CourseDetail.Success(courseTitle, result))
        }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        courseDetailEventChannel.send(CourseDetail.Error(message))
    }

    fun getAccessToken(): LiveData<String> = tokenPreferences.getAccessToken().asLiveData()

    fun getCurrentCourseId(): Int = courseId

    fun getCourseDetailById(id: Int) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getCourseDetail(id)
            .enqueue(object : Callback<DetailCourseResponse> {
                override fun onResponse(
                    call: Call<DetailCourseResponse>,
                    response: Response<DetailCourseResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()?.data!!.trxCourseCards
                        val courseTitle = response.body()?.data!!.title
                        onApiCallSuccess(courseTitle, result)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<DetailCourseResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    sealed class CourseDetail {
        object Loading : CourseDetail()
        data class Success(val courseTitle: String, val result: List<TrxCourseCardsItem>) :
            CourseDetail()

        data class Error(val message: String) : CourseDetail()
    }
}