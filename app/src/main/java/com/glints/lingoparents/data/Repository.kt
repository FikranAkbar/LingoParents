package com.glints.lingoparents.data

import androidx.lifecycle.LiveData
import com.glints.lingoparents.data.local.entity.UserEntity
import com.glints.lingoparents.data.remote.RemoteDataSource
import com.glints.lingoparents.data.remote.response.UserLoginResponse
import com.glints.lingoparents.data.remote.vo.ApiResponse
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

    override fun loginUser(email: String, password: String): LiveData<Resource<UserEntity>> {
        return object : NetworkBoundResource<UserEntity, UserLoginResponse>() {
            override fun createCall(): LiveData<ApiResponse<UserLoginResponse>> =
                remoteDataSource.loginUser(email, password)

            override fun mapRequestTypeToResultType(value: UserLoginResponse): UserEntity =
                Helper.mapUserLoginResponseToUserEntity(value)

        }.asLiveData()
    }


}