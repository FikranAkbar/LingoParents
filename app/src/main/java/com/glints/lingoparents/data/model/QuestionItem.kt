package com.glints.lingoparents.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionItem(
    var questionNumber: String,
    var studentAnswer: String,
    var correctAnswer: String
) : Parcelable
