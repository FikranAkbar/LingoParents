package com.glints.lingoparents.ui.insight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.model.response.AllInsightsListResponse
import com.glints.lingoparents.utils.ErrorUtils
import com.glints.lingoparents.utils.TokenPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InsightListViewModel(private val tokenPref: TokenPreferences) : ViewModel() {
    companion object {
        const val ALL_TAG = ""
        const val PARENTING_TAG = "parenting"
        const val LIFESTYLE_TAG = "lifestyle"
    }

    private val allInsightListChannel = Channel<AllInsightList>()
    val allInsightList = allInsightListChannel.receiveAsFlow()

    private val parentingInsightListChannel = Channel<ParentingInsightList>()
    val parentingInsightList = parentingInsightListChannel.receiveAsFlow()

    private val lifestyleInsightListChannel = Channel<LifestyleInsightList>()
    val lifestyleInsightList = lifestyleInsightListChannel.receiveAsFlow()

    fun onAllInsightItemClick(id: Int) = viewModelScope.launch {
        allInsightListChannel.send(AllInsightList.NavigateToDetailInsightFragment(id))
    }

    fun onParentingItemClick(id: Int) = viewModelScope.launch {
        parentingInsightListChannel.send(ParentingInsightList.NavigateToDetailInsightFragment(id))
    }

    fun onLifestyleInsightItemClick(id: Int) = viewModelScope.launch {
        lifestyleInsightListChannel.send(LifestyleInsightList.NavigateToDetailInsightFragment(id))
    }

    private fun onApiCallStarted(tag: String) = viewModelScope.launch {
        when (tag) {
            ALL_TAG -> {
                allInsightListChannel.send(AllInsightList.Loading)
            }
            PARENTING_TAG -> {
                parentingInsightListChannel.send(ParentingInsightList.Loading)
            }
            LIFESTYLE_TAG -> {
                lifestyleInsightListChannel.send(LifestyleInsightList.Loading)
            }
        }
    }

    private fun onApiCallSuccess(tag: String, list: List<AllInsightsListResponse.Message>) =
        viewModelScope.launch {
            when (tag) {
                ALL_TAG -> {
                    allInsightListChannel.send(AllInsightList.Success(list))
                }
                PARENTING_TAG -> {
                    parentingInsightListChannel.send(ParentingInsightList.Success(list))
                }
                LIFESTYLE_TAG -> {
                    lifestyleInsightListChannel.send(LifestyleInsightList.Success(list))
                }
            }
        }

    private fun onApiCallError(tag: String, message: String) = viewModelScope.launch {
        when (tag) {
            ALL_TAG -> {
                allInsightListChannel.send(AllInsightList.Error(message))
            }
            PARENTING_TAG -> {
                parentingInsightListChannel.send(ParentingInsightList.Error(message))
            }
            LIFESTYLE_TAG -> {
                lifestyleInsightListChannel.send(LifestyleInsightList.Error(message))
            }
        }
    }

    fun loadInsightList(tag: String) = viewModelScope.launch {
        onApiCallStarted(tag)
        APIClient
            .service
            .getAllInsightList(mapOf("tag" to tag))
            .enqueue(object : Callback<AllInsightsListResponse> {
                override fun onResponse(
                    call: Call<AllInsightsListResponse>,
                    response: Response<AllInsightsListResponse>
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess(tag, response.body()?.message!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(tag, apiError.message())
                    }
                }

                override fun onFailure(call: Call<AllInsightsListResponse>, t: Throwable) {
                    onApiCallError(tag, "Network Failed...")
                }
            })
    }

    fun getInsightSearchList(tag: String, keyword: String) = viewModelScope.launch {
        onApiCallStarted(tag)
        APIClient
            .service
            .getInsightSearchList(mapOf("tag" to tag, "keyword" to keyword))
            .enqueue(object : Callback<AllInsightsListResponse>{
                override fun onResponse(
                    call: Call<AllInsightsListResponse>,
                    response: Response<AllInsightsListResponse>,
                ) {
                    if (response.isSuccessful) {
                        onApiCallSuccess(tag, response.body()?.message!!)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(tag, apiError.message())
                    }
                }

                override fun onFailure(call: Call<AllInsightsListResponse>, t: Throwable) {
                    onApiCallError(tag, "Network Failed...")
                }

            })
    }

    fun sendKeywordToInsightListFragment(keyword: String) = viewModelScope.launch {
        EventBus.getDefault()
            .post(InsightSearchList.SendKeywordToInsightListFragment(keyword))
    }

    fun sendBlankKeywordToInsightListFragment() = viewModelScope.launch {
        EventBus.getDefault()
            .post(InsightSearchList.SendBlackKeywordToInsightListFragment)
    }

    sealed class InsightSearchList {
        data class SendKeywordToInsightListFragment(val keyword: String) : InsightSearchList()
        object SendBlackKeywordToInsightListFragment : InsightSearchList()
    }

    sealed class AllInsightList {
        object Loading : AllInsightList()
        data class Success(val list: List<AllInsightsListResponse.Message>) : AllInsightList()
        data class Error(val message: String) : AllInsightList()
        data class NavigateToDetailInsightFragment(val id: Int) : AllInsightList()
    }

    sealed class ParentingInsightList {
        object Loading : ParentingInsightList()
        data class Success(val list: List<AllInsightsListResponse.Message>) : ParentingInsightList()
        data class Error(val message: String) : ParentingInsightList()
        data class NavigateToDetailInsightFragment(val id: Int) : ParentingInsightList()
    }

    sealed class LifestyleInsightList {
        object Loading : LifestyleInsightList()
        data class Success(val list: List<AllInsightsListResponse.Message>) : LifestyleInsightList()
        data class Error(val message: String) : LifestyleInsightList()
        data class NavigateToDetailInsightFragment(val id: Int) : LifestyleInsightList()
    }
}