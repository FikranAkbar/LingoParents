package com.glints.lingoparents.utils

import com.glints.lingoparents.data.api.APIClient
import com.glints.lingoparents.data.api.APIError
import com.glints.lingoparents.data.api.APIErrorWithStatusAsString
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

object ErrorUtils {
    fun parseError(response: Response<*>): APIError {
        val converter: Converter<ResponseBody, APIError> = APIClient.retrofit()
            .responseBodyConverter(APIError::class.java, arrayOfNulls<Annotation>(0))

        val error: APIError

        try {
            error = converter.convert(response.errorBody()!!)!!
        } catch (e: IOException) {
            return APIError()
        }

        return error
    }

    fun parseErrorWithStatusAsString(response: Response<*>): APIErrorWithStatusAsString {
        val converter: Converter<ResponseBody, APIErrorWithStatusAsString> = APIClient.retrofit()
            .responseBodyConverter(APIErrorWithStatusAsString::class.java, arrayOfNulls<Annotation>(0))

        val error: APIErrorWithStatusAsString

        try {
            error = converter.convert(response.errorBody()!!)!!
        } catch (e: IOException) {
            return APIErrorWithStatusAsString()
        }

        return error
    }
}