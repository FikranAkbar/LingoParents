package com.glints.lingoparents.data.model.response

class AllInsightsListResponse{
    val status: String? = null
    val message: List<Message>? = null

    data class Message(
        val Trx_insight_tags: List<TrxInsightTag>,
        val content: String,
        val cover: Any,
        val createdAt: String,
        val id: Int,
        val idUser_create: Int,
        val idUser_update: Any,
        val is_active: String,
        val title: String,
        val total_dislike: Int,
        val total_like: Int,
        val total_report: Any,
        val total_views: Int,
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
        val updatedAt: String
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