package com.glints.lingoparents.utils

import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout

object AuthFormValidator {
    private const val PASSWORD_MIN_SIZE = 8

    const val EMAIL_WRONG_FORMAT_ERROR = "Mush be filled and type of email"
    const val PASSWORD_EMPTY_ERROR = "Passowrd must be minimum $PASSWORD_MIN_SIZE character"
    const val PASSWORD_DIFFERENCE_ERROR = "Confirm password is different from password"
    const val EMPTY_FIELD_ERROR = "Field must not be empty"

    fun <T> showFieldError(v: T, errorText: String? = null) {
        when (v) {
            is TextInputLayout -> {
                v.isErrorEnabled = true
                v.error = errorText
            }
        }
    }

    fun <T> hideFieldError(v: T) {
        when (v) {
            is ArrayList<*> -> {
                v.map {
                    (it as TextInputLayout).apply {
                        isErrorEnabled = false
                        error = ""
                    }
                }
            }
            is TextInputLayout -> {
                v.isErrorEnabled = false
                v.error = ""
            }
        }
    }

    fun isValidEmail(text: String?): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(text as CharSequence).matches() && text.isNotEmpty()

    fun isValidPassword(text: String?): Boolean {
        val length = text?.length ?: 0
        return length >= PASSWORD_MIN_SIZE
    }

    fun isValidField(text: String?): Boolean = text?.length!! > 0
}