package com.glints.lingoparents.data.model.response

import com.google.gson.annotations.SerializedName
import retrofit2.http.Field

data class CreateOrderResponse(
    val status: String,
    val message: String,
    val data: OrderData
) {
    data class OrderData(
        val id: Long,
        @SerializedName("id_event")
        val idEvent: Int,
        @SerializedName("id_user")
        val idUser: Int,
        @SerializedName("fullname")
        val fullName: String,
        val phone: String,
        val email: String,
        @SerializedName("total_price")
        val totalPrice: Int,
        @SerializedName("voucher_code")
        val voucherCode: String,
        @SerializedName("snap_token")
        val snapToken: String,
        @SerializedName("idUser_create")
        val idUserCreate: Int,
        val updatedAt: String,
        val createdAt: String
    )
}

data class CreateOrderData(
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone: String,
    val id_event: Int,
    val id_user: Int,
    val price: Int,
    val voucher_code: String?
)