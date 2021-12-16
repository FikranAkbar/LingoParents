package com.glints.lingoparents.data.model.response

import com.google.gson.annotations.SerializedName

data class LiveEventRegisterResponse(

	//detail
	@field:SerializedName("total_price")
	val totalPrice: Int,

	//get parent profile
	@field:SerializedName("phone")
	val phone: String,

	@field:SerializedName("voucher_code")
	val voucherCode: String,

	//datastore
	@field:SerializedName("id_user")
	val idUser: Int,

	//detail
	@field:SerializedName("id_event")
	val idEvent: Int,

	//get parent profle
	@field:SerializedName("fullname")
	val fullname: String,

	//detail
	@field:SerializedName("attendance_time")
	val attendanceTime: String,

	//datastore
	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("attendance")
	val attendance: String,

	@field:SerializedName("payment_method")
	val paymentMethod: String,

	@field:SerializedName("idUser_create")
	val idUserCreate: Int,

	@field:SerializedName("status")
	val status: String = "Success"
)
