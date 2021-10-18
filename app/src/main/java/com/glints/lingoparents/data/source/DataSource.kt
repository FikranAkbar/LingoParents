package com.glints.lingoparents.data.source

import androidx.lifecycle.LiveData
import com.glints.lingoparents.data.source.local.entity.UserEntity
import com.glints.lingoparents.vo.Resource

interface DataSource {
    fun loginUser(email: String, password: String): LiveData<Resource<UserEntity>>
}