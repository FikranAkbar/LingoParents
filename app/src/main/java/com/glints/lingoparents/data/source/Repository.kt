package com.glints.lingoparents.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.glints.lingoparents.data.source.local.entity.UserEntity
import com.glints.lingoparents.data.source.remote.RemoteDataSource
import com.glints.lingoparents.data.source.remote.body.UserLogin
import com.glints.lingoparents.data.source.remote.response.UserLoginResponse
import com.glints.lingoparents.data.source.remote.vo.ApiResponse
import com.glints.lingoparents.utils.Helper
import com.glints.lingoparents.vo.Resource

class Repository private constructor(private val remoteDataSource: RemoteDataSource) : DataSource {

    companion object {
        @Volatile
        private var instance: Repository? = null

        fun getInstance(remoteDataSource: RemoteDataSource): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(remoteDataSource)
            }
    }

    override fun userLogin(email: String, password: String): LiveData<Resource<UserEntity>> {
        return object : NetworkBoundResource<UserEntity, UserLoginResponse>() {
            override fun createCall(): LiveData<ApiResponse<UserLoginResponse>> =
                remoteDataSource.loginUser(email, password)

            override fun mapRequestTypeToResultType(value: UserLoginResponse): UserEntity =
                Helper.mapUserLoginResponseToUserEntity(value)

        }.asLiveData()
    }


}