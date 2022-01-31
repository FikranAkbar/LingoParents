package com.glints.lingoparents.data.model.response

class StudentCharacterResponse{
    val data: Data? = null
    val message: String? = null
    val status: String? = null

    data class Data(
        val character: String,
        val description: String,
        val logo: String,
        val personality_traits: String,
        val photo: String
    )
}