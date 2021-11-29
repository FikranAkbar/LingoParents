package com.glints.lingoparents.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SessionItem(
    var session: String,
    var shortDesc: String,
    var desc: String,
    var score: String,
) : Parcelable
