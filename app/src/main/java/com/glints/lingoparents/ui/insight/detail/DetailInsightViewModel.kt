package com.glints.lingoparents.ui.insight.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.InsightDetailResponse
import com.glints.lingoparents.data.model.response.InsightLikeDislikeResponse
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailInsightViewModel(
    private val tokenPref: TokenPreferences,
    private val insightId: Int
) : ViewModel() {

    companion object {
        const val INSIGHT_TYPE = "insight"
        const val COMMENT_TYPE = "comment"
        const val LIKE_ACTION = "like"
        const val DISLIKE_ACTION = "dislike"
    }

    private val insightDetailChannel = Channel<InsightDetail>()
    val insightDetail = insightDetailChannel.receiveAsFlow()

    private val likeDislikeInsightChannel = Channel<LikeDislikeInsight>()
    val likeDislikeInsight = likeDislikeInsightChannel.receiveAsFlow()

    fun onLikeDislikeOnClick(action: String, id: Int, type: String) = viewModelScope.launch {
        likeDislikeInsightChannel.send(LikeDislikeInsight.TryToLikeDislikeInsight(action, id, type))
        Log.d("TEST", "send try to like dislike insight")
    }

    private fun onApiCallStarted() = viewModelScope.launch {
        insightDetailChannel.send(InsightDetail.Loading)
    }

    private fun onApiCallStartedLikeDislike(type: String) = viewModelScope.launch {
        when (type) {
            INSIGHT_TYPE -> {
                Log.d("LikeInsight", "Loading")
                likeDislikeInsightChannel.send(LikeDislikeInsight.Loading)
            }
        }
    }

    private fun onApiCallSuccess(
        result: InsightDetailResponse.Message,
        list: List<InsightDetailResponse.MasterComment>
    ) = viewModelScope.launch {
        insightDetailChannel.send(InsightDetail.Success(result))
        insightDetailChannel.send(InsightDetail.SuccessGetComment(list))
    }

    private fun onApiCallSuccessLikeDislike(result: InsightLikeDislikeResponse) =
        viewModelScope.launch {
            Log.d("LikeInsight", "Success")
            likeDislikeInsightChannel.send(LikeDislikeInsight.Success(result))
        }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        insightDetailChannel.send(InsightDetail.Error(message))
    }

    private fun onApiCallErrorLikeDislike(type: String, message: String) = viewModelScope.launch {
        when (type) {
            INSIGHT_TYPE -> {
                Log.d("LikeInsight", "Error")
                likeDislikeInsightChannel.send(LikeDislikeInsight.Error(message))
            }
        }
    }

    fun loadInsightDetail(id: Int, accessToken: String) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getInsightDetail(id, accessToken)
            .enqueue(object : Callback<InsightDetailResponse> {
                override fun onResponse(
                    call: Call<InsightDetailResponse>,
                    response: Response<InsightDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()?.message!!
                        onApiCallSuccess(result, result.Master_comments)
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

    /*
    fun sendLikeRequest(id: Int, type: String, accessToken: String) = viewModelScope.launch {
        onApiCallStartedLikeDislike(type)
        APIClient
            .service
            .likeInsightDetail(id, type, accessToken)
            .enqueue(object : Callback<InsightLikeDislikeResponse> {
                override fun onResponse(
                    call: Call<InsightLikeDislikeResponse>,
                    response: Response<InsightLikeDislikeResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccessLikeDislike(response.body()!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<InsightLikeDislikeResponse>, t: Throwable) {
                    onApiCallErrorLikeDislike(type, "Network Failed...")
                }
            })
    }
     */

    /*
    fun sendDislikeRequest(id: Int, type: String, accessToken: String) = viewModelScope.launch {
        onApiCallStartedLikeDislike(type)
        APIClient
            .service
            .dislikeInsightDetail(id, type, accessToken)
            .enqueue(object : Callback<InsightLikeDislikeResponse> {
                override fun onResponse(
                    call: Call<InsightLikeDislikeResponse>,
                    response: Response<InsightLikeDislikeResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccessLikeDislike(response.body()!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<InsightLikeDislikeResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }
     */

    fun insightDetailLike(id: Int, type: String, accessToken: String) = viewModelScope.launch {
        onApiCallStartedLikeDislike(type)
        APIClient
            .service
            .likeInsightDetail(id, type, accessToken)
            .enqueue(object : Callback<InsightLikeDislikeResponse> {
                override fun onResponse(
                    call: Call<InsightLikeDislikeResponse>,
                    response: Response<InsightLikeDislikeResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccessLikeDislike(response.body()!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<InsightLikeDislikeResponse>, t: Throwable) {
                    onApiCallErrorLikeDislike(type, "Network Failed...")
                }
            })
    }


    fun insightDetailDislike(id: Int, type: String, accessToken: String) = viewModelScope.launch {
        APIClient
            .service
            .dislikeInsightDetail(id, type, accessToken)
            .enqueue(object : Callback<InsightLikeDislikeResponse> {
                override fun onResponse(
                    call: Call<InsightLikeDislikeResponse>,
                    response: Response<InsightLikeDislikeResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccessLikeDislike(response.body()!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<InsightLikeDislikeResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    fun getAccessToken(): LiveData<String> = tokenPref.getAccessToken().asLiveData()
    fun getCurrentInsightId(): Int = insightId

    sealed class InsightDetail {
        object Loading : InsightDetail()
        data class Success(val result: InsightDetailResponse.Message) : InsightDetail()
        data class SuccessGetComment(val list: List<InsightDetailResponse.MasterComment>) :
            InsightDetail()

        data class Error(val message: String) : InsightDetail()
    }

    sealed class LikeDislikeInsight {
        data class TryToLikeDislikeInsight(val action: String, val id: Int, val type: String) :
            LikeDislikeInsight()

        object Loading : LikeDislikeInsight()
        data class Success(val result: InsightLikeDislikeResponse) : LikeDislikeInsight()
        data class Error(val message: String) : LikeDislikeInsight()
    }
}