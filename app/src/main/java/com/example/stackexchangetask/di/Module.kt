package com.example.stackexchangetask.di

import com.example.stackexchangetask.api.RetrofitInstance
import com.example.stackexchangetask.api.StackExchangeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun providesApiClient(): RetrofitInstance = RetrofitInstance()

    @Provides
    @Singleton
    fun providesStackApi(retrofitInstance: RetrofitInstance): StackExchangeApi =
        retrofitInstance.retrofit.create(StackExchangeApi::class.java)

}