package com.glints.lingoparents.data.model.response

import com.glints.lingoparents.data.model.InsightCommentItem

class GetCommentRepliesResponse{
    val status: String? = null
    val message: List<Message>? = null

    data class Message(
        val Master_reports: List<MasterReport>,
        val Master_user: MasterUser,
        val comment: String,
        val commentable_type: String,
        val createdAt: String,
        val id: Int,
        val idUser_create: Int,
        val idUser_update: Int,
        val id_commentable: Int,
        val id_user: Int,
        val replies: Int,
        val status: String,
        val total_dislike: Int,
        val total_like: Int,
        val total_report: Int,
        val updatedAt: String,
        var is_liked: Int,
        var is_disliked: Int,
    )

    data class MasterReport(
        val createdAt: String,
        val id: Int,
        val idUser_create: Int,
        val idUser_update: Int,
        val id_reportable: Int,
        val id_user: Int,
        val report_comment: String,
        val reportable_type: String,
        val updatedAt: String
    )

    data class MasterUser(
        val Master_parent: MasterParent?,
        val Master_student: MasterStudent?,
        val Master_tutor: Any,
        val id: Int,
        val role: String
    )

    data class MasterParent(
        val address: String,
        val createdAt: String,
        val date_birth: String,
        val firstname: String,
        val gender: String,
        val id: Int,
        val idUser_create: Int,
        val id_occupation: Int,
        val is_active: String,
        val lastname: String,
        val phone: String,
        val photo: String,
        val referral_code: String,
        val updatedAt: String
    )

    data class MasterStudent(
        val address: String,
        val createdAt: String,
        val date_birth: String,
        val firstname: String,
        val fullname: String,
        val gender: String,
        val id: Int,
        val idUser_create: Int,
        val id_character: Int,
        val id_level: Int,
        val id_sublevel: Int,
        val is_active: String,
        val lastname: String,
        val phone: String,
        val photo: String,
        val referral_code: String,
        val updatedAt: String
    )
}

fun GetCommentRepliesResponse.mapToInsightCommentItems(): List<InsightCommentItem>? {
    val sortedByCreatedDateDescending = this.message?.reversed()
    val result = sortedByCreatedDateDescending?.map {
        return@map InsightCommentItem(
            idComment = it.id,
            idUser = it.id_user,
            photo = it.Master_user.Master_parent?.photo,
            name = "${it.Master_user.Master_parent?.firstname} ${it.Master_user.Master_parent?.lastname}",
            comment = it.comment,
            totalLike = it.total_like,
            totalDislike = it.total_dislike,
            totalReply = it.replies,
            is_liked = it.is_liked,
            is_disliked = it.is_disliked,
        )
    }

    result?.let {
        return it
    }

    return null
}