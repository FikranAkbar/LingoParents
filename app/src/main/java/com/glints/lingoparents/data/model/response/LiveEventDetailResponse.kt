package com.glints.lingoparents.data.model.response

class LiveEventDetailResponse {
    val status: String? = null
    val message: String? = null
    val data: LiveEventDetailItemResponse? = null

    data class LiveEventDetailItemResponse(
        val id: Int,
        val title: String,
        val cover: String? = null,
        val description: String,
        val date: String,
        val price: String,
        val speaker: String,
        val speaker_photo: String,
        val speaker_profession: String,
        val speaker_company: String,
        val status: String,
        val started_at: String,
        val idUser_create: Int,
        val Trx_event_participants: List<LiveEventParticipants>?
    )

    data class LiveEventParticipants(
        val id_user: Int
    )
}
