package com.glints.lingoparents.data.model.response

data class LinkedAccountsResponse(
    val status: String,
    val message: String,
    var data: List<ChildrenData>? = null
) {
    data class ChildrenData(
        val id: Int,
        val parent_relationship: String,
        val status: String,
        val Master_student: MasterStudent
    )

    data class MasterStudent(
        val fullname: String,
        val referral_code: String,
        val firstname: String,
        val lastname: String,
        val date_birth: String,
        val photo: String,
        val age: Int
    )
}