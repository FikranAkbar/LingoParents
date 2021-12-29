package com.glints.lingoparents.ui.insight.detail

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

class DetailInsightViewModel(
    private val tokenPref: TokenPreferences,
    private val insightId: Int,
) : ViewModel() {
    private var commentId: Int = 0

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

    private val commentRepliesChannel = Channel<GetCommentReplies>()
    val getCommentReplies = commentRepliesChannel.receiveAsFlow()

    private val deleteCommentChannel = Channel<DeleteComment>()
    val deleteComment = deleteCommentChannel.receiveAsFlow()

    private val updateCommentChannel = Channel<UpdateComment>()
    val updateComment = updateCommentChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        insightDetailChannel.send(InsightDetail.Loading)
    }

    private fun onApiCallStartedLikeDislike() = viewModelScope.launch {
        likeDislikeInsightChannel.send(LikeDislikeInsight.Loading)
    }

    private fun onApiCallStartedCreateComment() = viewModelScope.launch {
        createCommentChannel.send(CreateComment.Loading)
    }

    private fun onApiCallStartedGetCommentReplies() = viewModelScope.launch {
        commentRepliesChannel.send(GetCommentReplies.Loading)
    }

    private fun onApiCallStartedDeleteComment() = viewModelScope.launch {
        deleteCommentChannel.send(DeleteComment.Loading)
    }

    private fun onApiCallStartedUpdateComment() = viewModelScope.launch {
        updateCommentChannel.send(UpdateComment.Loading)
    }

    private fun onApiCallSuccess(
        result: InsightDetailResponse.Message,
        list: List<InsightDetailResponse.MasterComment>,
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

    private fun onApiCallSuccessGetCommentReplies(list: List<GetCommentRepliesResponse.Message>) =
        viewModelScope.launch {
            commentRepliesChannel.send(GetCommentReplies.Success(list))
        }

    private fun onApiCallSuccessDeleteComment(result: DeleteCommentResponse) =
        viewModelScope.launch {
            deleteCommentChannel.send(DeleteComment.Success(result))
        }

    private fun onApiCallSuccessUpdateComment(result: UpdateCommentResponse) =
        viewModelScope.launch {
            updateCommentChannel.send(UpdateComment.Success(result))
        }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        insightDetailChannel.send(InsightDetail.Error(message))
    }

    private fun onApiCallErrorLikeDislike(message: String) = viewModelScope.launch {
        likeDislikeInsightChannel.send(LikeDislikeInsight.Error(message))
    }

    private fun onApiCallErrorCreateComment(message: String) = viewModelScope.launch {
        createCommentChannel.send(CreateComment.Error(message))
    }

    private fun onApiCallErrorGetCommentReplies(message: String) = viewModelScope.launch {
        commentRepliesChannel.send(GetCommentReplies.Error(message))
    }

    private fun onApiCallErrorDeleteComment(message: String) = viewModelScope.launch {
        deleteCommentChannel.send(DeleteComment.Error(message))
    }

    private fun onApiCallErrorUpdateComment(message: String) = viewModelScope.launch {
        updateCommentChannel.send(UpdateComment.Error(message))
    }

    fun loadInsightDetail(id: Int) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getInsightDetail(id)
            .enqueue(object : Callback<InsightDetailResponse> {
                override fun onResponse(
                    call: Call<InsightDetailResponse>,
                    response: Response<InsightDetailResponse>,
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()?.message!!
                        val commentId = result.Master_comments
                        for (comment in commentId) {
                            saveCommentId(comment.id)
                        }
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
        onApiCallStartedLikeDislike()
        APIClient
            .service
            .likeInsightDetail(id, type)
            .enqueue(object : Callback<InsightLikeDislikeResponse> {
                override fun onResponse(
                    call: Call<InsightLikeDislikeResponse>,
                    response: Response<InsightLikeDislikeResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccessLikeDislike(response.body()!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallErrorLikeDislike(apiError.message())
                    }
                }

                override fun onFailure(call: Call<InsightLikeDislikeResponse>, t: Throwable) {
                    onApiCallErrorLikeDislike("Network Failed...")
                }
            })
    }

    fun sendDislikeRequest(id: Int, type: String) = viewModelScope.launch {
        onApiCallStartedLikeDislike()
        APIClient
            .service
            .dislikeInsightDetail(id, type)
            .enqueue(object : Callback<InsightLikeDislikeResponse> {
                override fun onResponse(
                    call: Call<InsightLikeDislikeResponse>,
                    response: Response<InsightLikeDislikeResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccessLikeDislike(response.body()!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallErrorLikeDislike(apiError.message())
                    }
                }

                override fun onFailure(call: Call<InsightLikeDislikeResponse>, t: Throwable) {
                    onApiCallErrorLikeDislike("Network Failed...")
                }
            })
    }

    fun createComment(id: Int, type: String, comment: String) = viewModelScope.launch {
        onApiCallStartedCreateComment()
        APIClient
            .service
            .createComment(id, type, comment)
            .enqueue(object : Callback<CreateCommentResponse> {
                override fun onResponse(
                    call: Call<CreateCommentResponse>,
                    response: Response<CreateCommentResponse>,
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

    fun getCommentReplies(id: Int) = viewModelScope.launch {
        onApiCallStartedGetCommentReplies()
        APIClient
            .service
            .getCommentReplies(id)
            .enqueue(object : Callback<GetCommentRepliesResponse> {
                override fun onResponse(
                    call: Call<GetCommentRepliesResponse>,
                    response: Response<GetCommentRepliesResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccessGetCommentReplies(response.body()?.message!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallErrorGetCommentReplies(apiError.message())
                    }
                }

                override fun onFailure(call: Call<GetCommentRepliesResponse>, t: Throwable) {
                    onApiCallErrorGetCommentReplies("Network Failed...")
                }
            })
    }

    fun deleteComment(id: Int) = viewModelScope.launch {
        onApiCallStartedDeleteComment()
        APIClient
            .service
            .deleteComment(id)
            .enqueue(object : Callback<DeleteCommentResponse> {
                override fun onResponse(
                    call: Call<DeleteCommentResponse>,
                    response: Response<DeleteCommentResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccessDeleteComment(response.body()!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallErrorDeleteComment(apiError.message())
                    }
                }

                override fun onFailure(call: Call<DeleteCommentResponse>, t: Throwable) {
                    onApiCallErrorDeleteComment("Network Failed...")
                }
            })
    }

    fun updateComment(id: Int, comment: String) = viewModelScope.launch {
        onApiCallStartedUpdateComment()
        APIClient
            .service
            .updateComment(id, comment)
            .enqueue(object : Callback<UpdateCommentResponse> {
                override fun onResponse(
                    call: Call<UpdateCommentResponse>,
                    response: Response<UpdateCommentResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccessUpdateComment(response.body()!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallErrorUpdateComment(apiError.message())
                    }
                }

                override fun onFailure(call: Call<UpdateCommentResponse>, t: Throwable) {
                    onApiCallErrorUpdateComment("Network Failed...")
                }
            })
    }

    fun getCurrentInsightId(): Int = insightId
    fun saveCommentId(id: Int) = viewModelScope.launch {
        commentId = id
    }

    fun getParentId() = tokenPref.getUserId().asLiveData()

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

    sealed class GetCommentReplies {
        object Loading : GetCommentReplies()
        data class Success(val list: List<GetCommentRepliesResponse.Message>) : GetCommentReplies()
        data class Error(val message: String) : GetCommentReplies()
    }

    sealed class DeleteComment {
        object Loading : DeleteComment()
        data class Success(val result: DeleteCommentResponse) : DeleteComment()
        data class Error(val message: String) : DeleteComment()
    }

    sealed class UpdateComment {
        object Loading : UpdateComment()
        data class Success(val result: UpdateCommentResponse) : UpdateComment()
        data class Error(val message: String) : UpdateComment()
    }

}