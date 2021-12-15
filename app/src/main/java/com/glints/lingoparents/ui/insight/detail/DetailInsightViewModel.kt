package com.glints.lingoparents.ui.insight.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.CreateCommentResponse
import com.glints.lingoparents.data.model.response.InsightDetailResponse
import com.glints.lingoparents.data.model.response.InsightLikeDislikeResponse
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
    private val insightId: Int
) : ViewModel() {

    companion object {
        const val INSIGHT_TYPE = "insight"
        const val COMMENT_TYPE = "comment"
    }

    private val insightDetailChannel = Channel<InsightDetail>()
    val insightDetail = insightDetailChannel.receiveAsFlow()

    private val likeDislikeInsightChannel = Channel<LikeDislikeInsight>()
    val likeDislikeInsight = likeDislikeInsightChannel.receiveAsFlow()

    private val createCommentChannel = Channel<CreateComment>()
    val createComment = createCommentChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        insightDetailChannel.send(InsightDetail.Loading)
    }

    private fun onApiCallStartedLikeDislike(type: String) = viewModelScope.launch {
        likeDislikeInsightChannel.send(LikeDislikeInsight.Loading)
    }

    private fun onApiCallStartedCreateComment(type: String) = viewModelScope.launch {
        createCommentChannel.send(CreateComment.Loading)
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
            likeDislikeInsightChannel.send(LikeDislikeInsight.Success(result))
        }

    private fun onApiCallSuccessCreateComment(result: CreateCommentResponse) =
        viewModelScope.launch {
            createCommentChannel.send(CreateComment.Success(result))
        }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        insightDetailChannel.send(InsightDetail.Error(message))
    }

    private fun onApiCallErrorLikeDislike(type: String, message: String) = viewModelScope.launch {
        likeDislikeInsightChannel.send(LikeDislikeInsight.Error(message))
    }

    private fun onApiCallErrorCreateComment(message: String) = viewModelScope.launch {
        createCommentChannel.send(CreateComment.Error(message))
    }

    fun loadInsightDetail(id: Int) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getInsightDetail(id)
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

    fun sendLikeRequest(id: Int, type: String) = viewModelScope.launch {
        onApiCallStartedLikeDislike(type)
        APIClient
            .service
            .likeInsightDetail(id, type)
            .enqueue(object : Callback<InsightLikeDislikeResponse> {
                override fun onResponse(
                    call: Call<InsightLikeDislikeResponse>,
                    response: Response<InsightLikeDislikeResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccessLikeDislike(response.body()!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallErrorLikeDislike(type, apiError.message())
                    }
                }

                override fun onFailure(call: Call<InsightLikeDislikeResponse>, t: Throwable) {
                    onApiCallErrorLikeDislike(type, "Network Failed...")
                }
            })
    }

    fun sendDislikeRequest(id: Int, type: String) = viewModelScope.launch {
        onApiCallStartedLikeDislike(type)
        APIClient
            .service
            .dislikeInsightDetail(id, type)
            .enqueue(object : Callback<InsightLikeDislikeResponse> {
                override fun onResponse(
                    call: Call<InsightLikeDislikeResponse>,
                    response: Response<InsightLikeDislikeResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccessLikeDislike(response.body()!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallErrorLikeDislike(type, apiError.message())
                    }
                }

                override fun onFailure(call: Call<InsightLikeDislikeResponse>, t: Throwable) {
                    onApiCallErrorLikeDislike(type, "Network Failed...")
                }
            })
    }

    fun createComment(id: Int, type: String, comment: String) = viewModelScope.launch {
        onApiCallStartedCreateComment(type)
        APIClient
            .service
            .createComment(id, type, comment)
            .enqueue(object : Callback<CreateCommentResponse> {
                override fun onResponse(
                    call: Call<CreateCommentResponse>,
                    response: Response<CreateCommentResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccessCreateComment(response.body()!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallErrorCreateComment(apiError.message())
                    }
                }

                override fun onFailure(call: Call<CreateCommentResponse>, t: Throwable) {
                    onApiCallErrorCreateComment("Network Failed...")
                }
            })
    }

    fun getCurrentInsightId(): Int = insightId

    sealed class InsightDetail {
        object Loading : InsightDetail()
        data class Success(val result: InsightDetailResponse.Message) : InsightDetail()
        data class SuccessGetComment(val list: List<InsightDetailResponse.MasterComment>) :
            InsightDetail()

        data class Error(val message: String) : InsightDetail()
    }

    sealed class LikeDislikeInsight {
        object Loading : LikeDislikeInsight()
        data class Success(val result: InsightLikeDislikeResponse) : LikeDislikeInsight()
        data class Error(val message: String) : LikeDislikeInsight()
    }

    sealed class CreateComment {
        object Loading : CreateComment()
        data class Success(val result: CreateCommentResponse) : CreateComment()
        data class Error(val message: String) : CreateComment()
    }
}