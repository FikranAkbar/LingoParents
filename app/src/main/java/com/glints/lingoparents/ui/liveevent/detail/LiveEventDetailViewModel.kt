package com.glints.lingoparents.ui.liveevent.detail

import androidx.lifecycle.LiveData
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

class LiveEventDetailViewModel(
    private val tokenPreferences: TokenPreferences,
    private val eventId: Int
) : ViewModel() {
    private val liveEventDetailEventChannel = Channel<LiveEventDetailEvent>()
    val liveEventDetailEvent = liveEventDetailEventChannel.receiveAsFlow()

    private fun onApiCallStarted() = viewModelScope.launch {
        liveEventDetailEventChannel.send(LiveEventDetailEvent.Loading)
    }

    private fun onApiCallSuccess(result: LiveEventDetailResponse.LiveEventDetailItemResponse) =
        viewModelScope.launch {
            liveEventDetailEventChannel.send(LiveEventDetailEvent.Success(result))
        }

    private fun onRegisterApiCallSuccess() =
        viewModelScope.launch {
            liveEventDetailEventChannel.send(LiveEventDetailEvent.RegisterSuccess)
        }

    private fun onCreateEventOrderSuccess(snapToken: String) =
        viewModelScope.launch {
            liveEventDetailEventChannel.send(LiveEventDetailEvent.CreateOrderEventSuccess(snapToken))
        }

    private fun onGetProfileApiCallSuccess(result: ParentProfileResponse) =
        viewModelScope.launch {
            liveEventDetailEventChannel.send(LiveEventDetailEvent.SuccessGetProfile(result))
        }

    private fun onApiCallError(message: String) = viewModelScope.launch {
        liveEventDetailEventChannel.send(LiveEventDetailEvent.Error(message))
    }
    private fun onRegisterApiCallError(message: String) = viewModelScope.launch {
        liveEventDetailEventChannel.send(LiveEventDetailEvent.RegisterError(message))
    }

    fun getUserId(): LiveData<String> = tokenPreferences.getUserId().asLiveData()

    fun getAccessEmail(): LiveData<String> = tokenPreferences.getAccessEmail().asLiveData()

    fun getAccessToken(): LiveData<String> = tokenPreferences.getAccessToken().asLiveData()

    fun getCurrentEventId(): Int = eventId

    fun onRegisterButtonClick(
        fullname: String,
        email: String,
        phone: String,
        voucherCode: String,
        paymentMethod: String
    ) = viewModelScope.launch {
        liveEventDetailEventChannel.send(
            LiveEventDetailEvent.RegisterClick(
                fullname, email, phone, voucherCode, paymentMethod
            )
        )
    }

    fun getLiveEventDetailById(id: Int) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getLiveEventById(id)
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

    fun getParentProfile() = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .getParentProfile()
            .enqueue(object : Callback<ParentProfileResponse> {
                override fun onResponse(
                    call: Call<ParentProfileResponse>,
                    response: Response<ParentProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()!!
                        onGetProfileApiCallSuccess(result)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<ParentProfileResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }

    fun registerLiveEvent(
        totalPrice: Int,
        phone: String,
        voucherCode: String,
        idUser: Int,
        idEvent: Int,
        fullname: String,
        attendanceTime: String,
        email: String,
        attendance: String,
        paymentMethod: String,
        idUserCreate: Int,
        status: String
    ) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .registerLiveEvent(
                idUser,
                idEvent,
                fullname,
                phone,
                email,
                attendance,
                attendanceTime,
                idUserCreate,
                totalPrice,
                voucherCode,
                paymentMethod,
                status
            )
            .enqueue(object : Callback<LiveEventRegisterResponse> {
                override fun onResponse(
                    call: Call<LiveEventRegisterResponse>,
                    response: Response<LiveEventRegisterResponse>
                ) {
                    if (response.isSuccessful) {
                        onRegisterApiCallSuccess()
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onRegisterApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<LiveEventRegisterResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })

    }

    fun createOrderEvent(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        idEvent: Int,
        idUser: Int,
        price: Int,
        voucherCode: String
    ) = viewModelScope.launch {
        onApiCallStarted()
        APIClient
            .service
            .createEventOrder(CreateOrderData(
                firstName,
                lastName,
                email,
                phone,
                idEvent,
                idUser,
                price,
                voucherCode
            ))
            .enqueue(object : Callback<CreateOrderResponse> {
                override fun onResponse(
                    call: Call<CreateOrderResponse>,
                    response: Response<CreateOrderResponse>,
                ) {
                    if (response.isSuccessful) {
                        onCreateEventOrderSuccess(response.body()!!.data.snapToken)
                    } else {
                        val apiError = ErrorUtils.parseError(response)
                        onRegisterApiCallError(apiError.message())
                    }
                }

                override fun onFailure(call: Call<CreateOrderResponse>, t: Throwable) {
                    onApiCallError("Network Failed...")
                }
            })
    }


    sealed class LiveEventDetailEvent {
        object Loading : LiveEventDetailEvent()
        data class Success(val result: LiveEventDetailResponse.LiveEventDetailItemResponse) :
            LiveEventDetailEvent()

        data class Error(val message: String) : LiveEventDetailEvent()
        data class RegisterError(val message: String) : LiveEventDetailEvent()

        data class SuccessGetProfile(val parentProfile: ParentProfileResponse) :
            LiveEventDetailEvent()

        data class RegisterClick(
            val fullname: String,
            val email: String,
            val phone: String,
            val voucherCode: String,
            val paymentMethod: String
        ) :
            LiveEventDetailEvent()

        object RegisterSuccess : LiveEventDetailEvent()

        data class CreateOrderEventSuccess(val snapToken: String) : LiveEventDetailEvent()
    }
}