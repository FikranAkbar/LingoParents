package com.glints.lingoparents.data.model.response

data class StudentListResponse(
    val data: List<DataItem?>? = null,
    val message: String? = null,
    val status: String? = null
) {
    data class DataItem(
        val level: Any? = null,
        val name: String? = null,
        val sublevel: Any? = null,
        val studentId: Int? = null,
        val photo: String? = null,
        val relationship: String? = null,
        val age: Int? = null
    )
}