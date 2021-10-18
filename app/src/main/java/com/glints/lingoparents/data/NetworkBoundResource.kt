package com.glints.lingoparents.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.glints.lingoparents.data.remote.vo.ApiResponse
import com.glints.lingoparents.data.remote.vo.StatusResponse
import com.glints.lingoparents.vo.Resource

abstract class NetworkBoundResource<ResultType, RequestType> {
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        fetchFromNetwork()
    }

    private fun onFetchFailed() {}

    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>

    protected abstract fun mapRequestTypeToResultType(value: RequestType): ResultType

    private fun fetchFromNetwork() {
        val apiResponse = createCall()

        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            when(response.status) {
                StatusResponse.SUCCESS -> {
                    val newValue = mapRequestTypeToResultType(response.body)
                    result.value = Resource.success(newValue)
                }

                StatusResponse.ERROR -> {
                    onFetchFailed()
                    result.value = Resource.error(response.message, null)
                }
            }
        }
    }

    fun asLiveData(): LiveData<Resource<ResultType>> = result
}