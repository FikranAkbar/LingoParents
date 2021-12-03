package com.glints.lingoparents.data.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//class ParentProfileResponse {
//    val referral_code: String? = null
//    val firstname: String? = null
//    val lastname: String? = null
//    val address: String? = null
//    val phone: String? = null
//    val photo: String? = null
//} original
@Parcelize
data class ParentProfileResponse (
    val referral_code: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val photo: String? = null
) : Parcelable