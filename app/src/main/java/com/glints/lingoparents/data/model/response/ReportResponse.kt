package com.glints.lingoparents.data.model.response

class ReportResponse{
    val status: String? = null
    val message: Message? = null

    data class Message(
        val createdAt: String,
        val id: Int,
        val idUser_create: Int,
        val id_reportable: Int,
        val id_user: Int,
        val report_comment: String,
        val reportable_type: String,
        val updatedAt: String
    )
}