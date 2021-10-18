package com.glints.lingoparents.di

import com.glints.lingoparents.data.Repository
import com.glints.lingoparents.data.remote.RemoteDataSource

object Injection {

    fun provideRepository(): Repository {
        val remoteDataSource = RemoteDataSource.getInstance()
        return Repository.getInstance(remoteDataSource)
    }
}