package com.glints.lingoparents.data.model.response

data class InviteChildResponse(
    val status: String,
    val message: String,
    val data: ChildrenData
) {
    data class ChildrenData(
        val id: Int,
        val id_student: String,
        val id_parent: String,
        val parent_relationship: String,
        val status: String,
        val idUser_create: String,
        val createdAt: String,
        val updatedAt: String
    )
}
