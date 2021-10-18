package com.glints.lingoparents.utils

import com.glints.lingoparents.data.source.local.entity.UserEntity
import com.glints.lingoparents.data.source.remote.response.UserLoginResponse

object Helper {
    fun mapUserLoginResponseToUserEntity(response: UserLoginResponse): UserEntity {
        return UserEntity(
            response.id!!,
            response.username!!
        )
    }
}