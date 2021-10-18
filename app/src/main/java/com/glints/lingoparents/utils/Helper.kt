package com.glints.lingoparents.utils

import com.glints.lingoparents.data.local.entity.UserEntity
import com.glints.lingoparents.data.remote.response.UserLoginResponse

object Helper {
    fun mapUserLoginResponseToUserEntity(response: UserLoginResponse): UserEntity {
        return UserEntity(
            response.id!!,
            response.username!!
        )
    }
}