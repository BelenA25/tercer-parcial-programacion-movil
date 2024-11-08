package com.example.practicandoexamen.di

import android.content.Context
import com.example.data.MovieRepository
import com.example.framework.local.LocalDataSource
import com.example.framework.network.RemoteDataSource
import com.example.framework.network.RetrofitBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModules {


    @Provides
    fun localDataSource(@ApplicationContext context: Context) : LocalDataSource {
        return LocalDataSource(context = context)
    }


    @Provides
    fun provideRetrofit() : RetrofitBuilder {
        return RetrofitBuilder
    }


    @Provides
    @Singleton
    fun provideRemoteDataSource(
        retrofit: RetrofitBuilder
    ) : RemoteDataSource {
        return RemoteDataSource(retrofit = retrofit)
    }


    @Provides
    fun provideMovieRepository(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource
    ) : MovieRepository {
        return MovieRepository(remoteDataSource, localDataSource)
    }
}
