package com.glints.lingoparents.data.model.response

import com.google.gson.annotations.SerializedName

data class DetailCourseResponse(

    @field:SerializedName("data")
    val data: Data,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: String
)

data class TrxCourseCardsItem(

    @field:SerializedName("card_photo")
    val cardPhoto: String,

    @field:SerializedName("description")
    val description: String
)

//data class TrxCoursePricesItem(
//
//    @field:SerializedName("price_per_session")
//    val pricePerSession: Int,
//
//    @field:SerializedName("category")
//    val category: String
//)

data class Data(

    @field:SerializedName("cover")
    val cover: String,

    @field:SerializedName("flag")
    val flag: String,

    @field:SerializedName("description")
    val description: String,

//    @field:SerializedName("Trx_course_prices")
//    val trxCoursePrices: List<TrxCoursePricesItem>,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("Trx_course_cards")
    val trxCourseCards: List<TrxCourseCardsItem>
)
