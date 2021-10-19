package com.glints.lingoparents.data

import androidx.lifecycle.LiveData
import com.glints.lingoparents.data.model.UserEntity
import com.glints.lingoparents.vo.Resource

interface DataSource {
    fun loginUser(email: String, password: String): LiveData<Resource<UserEntity>>
}