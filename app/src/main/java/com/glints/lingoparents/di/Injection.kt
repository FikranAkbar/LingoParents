package com.glints.lingoparents.di

import com.glints.lingoparents.data.source.Repository
import com.glints.lingoparents.data.source.remote.RemoteDataSource

object Injection {

    fun provideRepository(): Repository {
        val remoteDataSource = RemoteDataSource.getInstance()
        return Repository.getInstance(remoteDataSource)
    }
}