package com.glints.lingoparents.data.model.response

class InsightDetailResponse{
    val status: String? = null
    val message: Message? = null

    data class Message(
        val Master_comments: List<MasterComment>,
        val Trx_insight_tags: List<TrxInsightTag>,
        val content: String,
        val cover: String,
        val createdAt: String,
        val id: Int,
        val idUser_create: Int,
        val idUser_update: Any,
        val is_active: String,
        val title: String,
        val total_dislike: Int,
        val total_like: Int,
        val total_report: Int,
        val total_views: Int,
        val updatedAt: String
    )

    data class MasterComment(
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
        val updatedAt: String
    )

    data class TrxInsightTag(
        val Master_tag: MasterTag,
        val createdAt: String,
        val id: Int,
        val idUser_create: Int,
        val idUser_update: Any,
        val id_insight: Int,
        val id_tag: Int,
        val updatedAt: Any
    )

    data class MasterTag(
        val createdAt: String,
        val id: Int,
        val idUser_create: Int,
        val idUser_update: Any,
        val tag_name: String,
        val updatedAt: Any
    )
}
