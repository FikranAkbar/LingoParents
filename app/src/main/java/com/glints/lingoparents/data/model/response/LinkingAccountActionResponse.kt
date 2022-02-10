package com.glints.lingoparents.data.model.response

data class LinkingAccountActionResponse(
    val status: String,
    val message: String,
    val data: ChildrenData
) {
    data class ChildrenData(
        val id: Int,
        val id_student: Int,
        val id_parent: Int,
        val parent_relationship: String,
        val status: String,
        val idUser_create: Int,
        val idUser_update: String,
        val Master_student: MasterStudent,
        val Master_parent: MasterParent,
        val updatedAt: String
    )

    data class MasterStudent(
        val firstname: String,
        val lastname: String,
        val phone: String,
        val Master_user: MasterUser
    )

    data class MasterUser(
        val email: String,
        val role: String
    )

    data class MasterParent(
        val firstname: String,
        val lastname: String,
        val phone: String
    )
}
