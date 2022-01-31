package com.glints.lingoparents.data.model.response

import com.glints.lingoparents.data.model.InsightCommentItem

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

fun CreateCommentResponse.Message.mapToInsightCommentItem(parentProfile: ParentProfileResponse): InsightCommentItem {
    return InsightCommentItem(
        idComment = this.id,
        idUser = this.id_user,
        photo = parentProfile.photo,
        name = "${parentProfile.firstname} ${parentProfile.lastname}",
        comment = this.comment,
        totalLike = 0,
        totalDislike = 0,
        totalReply = 0,
        is_liked = 0,
        is_disliked = 0
    )
}
