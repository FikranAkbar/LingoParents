package com.glints.lingoparents.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.glints.lingoparents.data.Repository
import com.glints.lingoparents.data.model.UserEntity
import com.glints.lingoparents.vo.Resource

class LoginViewModel(private val repository: Repository) : ViewModel() {
    fun loginUser(email: String, password: String): LiveData<Resource<UserEntity>> =
        repository.loginUser(email, password)
}