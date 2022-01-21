package com.glints.lingoparents.ui.insight.detail

import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.InsightCommentItem
import com.glints.lingoparents.data.model.response.*
import com.glints.lingoparents.databinding.ItemInsightCommentBinding
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
    companion object {
        const val INSIGHT_TYPE = "insight"
        const val COMMENT_TYPE = "comment"
    }

    private val insightDetailChannel = Channel<InsightDetail>()
    val insightDetail = insightDetailChannel.receiveAsFlow()

    private val actionInsightChannel = Channel<InsightAction>()
    val actionInsight = actionInsightChannel.receiveAsFlow()

    lateinit var parentProfile: ParentProfileResponse

    private fun onApiCallStarted() = viewModelScope.launch {
        insightDetailChannel.send(InsightDetail.Loading)
    }

    private fun onApiCallSuccess(
        result: InsightDetailResponse.Message,
        list: List<InsightCommentItem>,
    ) = viewModelScope.launch {
        insightDetailChannel.send(InsightDetail.Success(result))
        insightDetailChannel.send(InsightDetail.SuccessGetComment(list))
    }

    private fun onApiCallSuccessReport(result: ReportResponse) = viewModelScope.launch {
        actionInsightChannel.send(InsightAction.SuccessReport(result))
    }

    private fun onApiCallSuccessLikeDislike(
        result: InsightLikeDislikeResponse,
        tvCount: TextView,
        tvOtherCount: TextView,
    ) =
        viewModelScope.launch {
            actionInsightChannel.send(InsightAction.SuccessLikeDislike(result,
                tvCount,
                tvOtherCount))
        }

    private fun onApiCallSuccessCreateComment(
        result: InsightCommentItem,
        uniqueAdapterId: Double,
    ) =
        viewModelScope.launch {
            actionInsightChannel.send(InsightAction.SuccessCreateComment(result, uniqueAdapterId))
        }

    private fun onApiCallSuccessGetCommentReplies(
        list: List<InsightCommentItem>,
        uniqueAdapterId: Double,
        binding: ItemInsightCommentBinding
    ) =
        viewModelScope.launch {
            actionInsightChannel.send(InsightAction.SuccessGetCommentReplies(list, uniqueAdapterId, binding))
        }

    private fun onApiCallSuccessDeleteComment(
        result: DeleteCommentResponse,
        item: InsightCommentItem,
        uniqueAdapterId: Double
    ) =
        viewModelScope.launch {
            actionInsightChannel.send(InsightAction.SuccessDeleteComment(
                result,
                item,
                uniqueAdapterId
            ))
        }

    private fun onApiCallSuccessUpdateComment(result: UpdateCommentResponse, tvCommentBody: TextView, comment: String) =
        viewModelScope.launch {
            actionInsightChannel.send(InsightAction.SuccessUpdateComment(result, tvCommentBody, comment))
        }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        insightDetailChannel.send(InsightDetail.Error(message))
    }

    private fun onApiCallErrorReport(message: String) = viewModelScope.launch {
        actionInsightChannel.send(InsightAction.Error(message))
    }

    private fun onApiCallErrorAction(message: String) = viewModelScope.launch {
        actionInsightChannel.send(InsightAction.Error(message))
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
                        val result = response.body()!!

                        val comments = result.mapToInsightCommentItems()

                        onApiCallSuccess(result.message!!, comments!!)
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

    fun reportInsight(id: String, type: String, report_comment: String) = viewModelScope.launch {
        APIClient
            .service
            .reportInsight(mapOf("id" to id, "type" to type), report_comment)
            .enqueue(object : Callback<ReportResponse> {
                override fun onResponse(
                    call: Call<ReportResponse>,
                    response: Response<ReportResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccessReport(response.body()!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallErrorReport(apiError.message())
                    }
                }

                override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    fun sendLikeRequest(id: Int, type: String, tvLikeCount: TextView, tvDislikeCount: TextView) =
        viewModelScope.launch {
            APIClient
                .service
                .likeInsightDetail(id, type)
                .enqueue(object : Callback<InsightLikeDislikeResponse> {
                    override fun onResponse(
                        call: Call<InsightLikeDislikeResponse>,
                        response: Response<InsightLikeDislikeResponse>,
                    ) {
                        if (response.isSuccessful) {
                            onApiCallSuccessLikeDislike(response.body()!!,
                                tvCount = tvLikeCount,
                                tvOtherCount = tvDislikeCount)
                        } else {
                            val apiError = ErrorUtils.parseError(response)
                            onApiCallErrorAction(apiError.message())
                        }
                    }

                    override fun onFailure(call: Call<InsightLikeDislikeResponse>, t: Throwable) {
                        onApiCallErrorAction("Network Failed...")
                    }
                })
        }

    fun sendDislikeRequest(id: Int, type: String, tvDislikeCount: TextView, tvLikeCount: TextView) =
        viewModelScope.launch {
            APIClient
                .service
                .dislikeInsightDetail(id, type)
                .enqueue(object : Callback<InsightLikeDislikeResponse> {
                    override fun onResponse(
                        call: Call<InsightLikeDislikeResponse>,
                        response: Response<InsightLikeDislikeResponse>,
                    ) {
                        if (response.isSuccessful) {
                            onApiCallSuccessLikeDislike(response.body()!!,
                                tvCount = tvDislikeCount,
                                tvOtherCount = tvLikeCount)
                        } else {
                            val apiError = ErrorUtils.parseError(response)
                            onApiCallErrorAction(apiError.message())
                        }
                    }

                    override fun onFailure(call: Call<InsightLikeDislikeResponse>, t: Throwable) {
                        onApiCallErrorAction("Network Failed...")
                    }
                })
        }

    fun createComment(id: Int, uniqueAdapterId: Double, type: String, comment: String) =
        viewModelScope.launch {
            APIClient
                .service
                .createComment(id, type, comment)
                .enqueue(object : Callback<CreateCommentResponse> {
                    override fun onResponse(
                        call: Call<CreateCommentResponse>,
                        response: Response<CreateCommentResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()!!.message!!.mapToInsightCommentItem(parentProfile)
                            onApiCallSuccessCreateComment(result, uniqueAdapterId)
                        } else {
                            val apiError = ErrorUtils.parseError(response)
                            onApiCallErrorAction(apiError.message())
                        }
                    }

                    override fun onFailure(call: Call<CreateCommentResponse>, t: Throwable) {
                        onApiCallErrorAction("Network Failed...")
                    }
                })
        }

    fun getCommentReplies(id: Int, uniqueId: Double, binding: ItemInsightCommentBinding) = viewModelScope.launch {
        APIClient
            .service
            .getCommentReplies(id)
            .enqueue(object : Callback<GetCommentRepliesResponse> {
                override fun onResponse(
                    call: Call<GetCommentRepliesResponse>,
                    response: Response<GetCommentRepliesResponse>,
                ) {
                    if (response.isSuccessful) {
                        val comments = response.body()?.mapToInsightCommentItems()
                        onApiCallSuccessGetCommentReplies(comments!!, uniqueId, binding)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallErrorAction(apiError.message())
                    }
                }

                override fun onFailure(call: Call<GetCommentRepliesResponse>, t: Throwable) {
                    onApiCallErrorAction("Network Failed...")
                }
            })
    }

    fun deleteComment(
        item: InsightCommentItem,
        id: Int,
        uniqueAdapterId: Double,
    ) = viewModelScope.launch {
        APIClient
            .service
            .deleteComment(id)
            .enqueue(object : Callback<DeleteCommentResponse> {
                override fun onResponse(
                    call: Call<DeleteCommentResponse>,
                    response: Response<DeleteCommentResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccessDeleteComment(response.body()!!,
                            item,
                            uniqueAdapterId)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallErrorAction(apiError.message())
                    }
                }

                override fun onFailure(call: Call<DeleteCommentResponse>, t: Throwable) {
                    onApiCallErrorAction("Network Failed...")
                }
            })
    }

    fun updateComment(id: Int, comment: String, tvCommentBody: TextView) = viewModelScope.launch {
        APIClient
            .service
            .updateComment(id, comment)
            .enqueue(object : Callback<UpdateCommentResponse> {
                override fun onResponse(
                    call: Call<UpdateCommentResponse>,
                    response: Response<UpdateCommentResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccessUpdateComment(response.body()!!, tvCommentBody, comment)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallErrorAction(apiError.message())
                    }
                }

                override fun onFailure(call: Call<UpdateCommentResponse>, t: Throwable) {
                    onApiCallErrorAction("Network Failed...")
                }
            })
    }

    fun getCurrentInsightId(): Int = insightId

    fun getParentId() = tokenPref.getUserId().asLiveData()

    fun getParentProfile() = viewModelScope.launch {
        APIClient
            .service
            .getParentProfile()
            .enqueue(object : Callback<ParentProfileResponse> {
                override fun onResponse(
                    call: Call<ParentProfileResponse>,
                    response: Response<ParentProfileResponse>,
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()!!
                        parentProfile = result
                    } else {
                        if (response.code() != 400) {
                            val apiError = ErrorUtils.parseError(response)
                            onApiCallError(apiError.message())
                        } else {
                            onApiCallError("Something went wrong...")
                        }
                    }
                }

                override fun onFailure(call: Call<ParentProfileResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    sealed class InsightDetail {
        object Loading : InsightDetail()
        data class Success(val result: InsightDetailResponse.Message) : InsightDetail()
        data class SuccessGetComment(val list: List<InsightCommentItem>) :
            InsightDetail()

        data class Error(val message: String) : InsightDetail()
    }

    sealed class InsightAction {
        data class SuccessCreateComment(
            val result: InsightCommentItem,
            val uniqueAdapterId: Double,
        ) : InsightAction()

        data class SuccessDeleteComment(
            val result: DeleteCommentResponse,
            val item: InsightCommentItem,
            val uniqueAdapterId: Double
        ) : InsightAction()

        data class SuccessGetCommentReplies(
            val list: List<InsightCommentItem>,
            val uniqueAdapterId: Double,
            val binding: ItemInsightCommentBinding
        ) :
            InsightAction()

        data class SuccessLikeDislike(
            val result: InsightLikeDislikeResponse,
            val tvCount: TextView,
            val tvOtherCount: TextView,
        ) : InsightAction()

        data class SuccessUpdateComment(val result: UpdateCommentResponse, val tvCommentBody: TextView, val comment: String) : InsightAction()
        data class SuccessReport(val result: ReportResponse) : InsightAction()
        data class Error(val message: String) : InsightAction()
    }

}