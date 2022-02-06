package com.glints.lingoparents.data.api

class APIError {
    private val statusCode: Int = 0
    private val message: String = ""

    fun status(): Int {
        return statusCode
    }

    fun message(): String {
        return message
    }
}

class APIErrorWithStatusAsString {
    private val status: String = ""
    private val message: String = ""

    fun getStatus(): String {
        return status
    }

    fun getMessage(): String {
        return message
    }
}