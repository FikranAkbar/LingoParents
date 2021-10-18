package com.glints.lingoparents.data.source

import com.glints.lingoparents.data.source.remote.RemoteDataSource

class Repository private constructor(private val remoteDataSource: RemoteDataSource) : DataSource {

    companion object {
        @Volatile
        private var instance: Repository? = null

        fun getInstance(remoteDataSource: RemoteDataSource): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(remoteDataSource)
            }
    }
}