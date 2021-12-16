package com.glints.lingoparents.data.model.response

class CreateCommentResponse{
    val status: String? = null
    val message: Message? = null

    data class Message(
        val comment: String,
        val commentable_type: String,
        val createdAt: String,
        val id: Int,
        val idUser_create: Int,
        val id_commentable: Int,
        val id_user: Int,
        val status: String,
        val total_dislike: Int,
        val total_like: Int,
        val total_report: Int,
        val updatedAt: String
    )
}
