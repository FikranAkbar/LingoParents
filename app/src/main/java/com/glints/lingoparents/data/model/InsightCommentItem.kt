package com.glints.lingoparents.data.model

data class InsightCommentItem(
    val idComment: Int,
    val idUser: Int,
    val photo: String?,
    val name: String,
    val comment: String,
    val totalLike: Int,
    val totalDislike: Int,
    var totalReply: Int,
    var is_liked: Int,
    var is_disliked: Int,
)
