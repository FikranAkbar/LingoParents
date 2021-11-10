package com.glints.lingoparents.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CourseItem(
    var name: String,
    var desc1: String,
    var desc2: String,
    var desc3: String,
    var card1: Int,
    var card2: Int,
    var card3: Int,
    var image: Int
) : Parcelable
