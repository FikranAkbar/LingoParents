package com.glints.lingoparents.data.model.response

data class ChildrenSearchResponse(
    val status: String,
    val message: String,
    val data: List<ChildrenData>
) {
    data class ChildrenData(
        val id: Int,
        val referral_code: String,
        val fullname: String,
        val date_birth: String,
        val age: Int,
        val photo: String
    )
}
