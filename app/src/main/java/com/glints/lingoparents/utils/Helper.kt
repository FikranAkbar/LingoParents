package com.glints.lingoparents.utils

import com.glints.lingoparents.data.model.UserEntity
import com.glints.lingoparents.data.remote.response.UserLoginResponse

object Helper {
    fun mapUserLoginResponseToUserEntity(response: UserLoginResponse): UserEntity {
        return UserEntity(
            response.id!!,
            response.username!!
        )
    }
}