package com.glints.lingoparents.ui.insight.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.InsightDetailResponse
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailInsightViewModel(
    private val tokenPref: TokenPreferences,
    private val insightId: Int) : ViewModel() {

    private val insightDetailChannel = Channel<InsightDetail>()
    val insightDetail = insightDetailChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        insightDetailChannel.send(InsightDetail.Loading)
    }

    private fun onApiCallSuccess(result: InsightDetailResponse.Message) = viewModelScope.launch {
        insightDetailChannel.send(InsightDetail.Success(result))
    }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        insightDetailChannel.send(InsightDetail.Error(message))
    }

    fun loadInsightDetail(id: Int, accessToken: String) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getInsightDetail(id, accessToken)
            .enqueue(object: Callback<InsightDetailResponse> {
                override fun onResponse(
                    call: Call<InsightDetailResponse>,
                    response: Response<InsightDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()?.message!!
                        onApiCallSuccess(result)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<InsightDetailResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }

            })
    }

    fun getAccessToken(): LiveData<String> = tokenPref.getAccessToken().asLiveData()
    fun getCurrentInsightId(): Int  = insightId

    sealed class InsightDetail{
        object Loading: InsightDetail()
        data class Success(val result: InsightDetailResponse.Message): InsightDetail()
        data class Error(val message: String): InsightDetail()
    }
}