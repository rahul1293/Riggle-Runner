package com.rk_tech.riggle_runner.di.module

import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.api.ApiHelperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Singleton
    @Provides
    fun providesMainRepo(apiHelper: ApiHelperImpl): ApiHelper =
        apiHelper

}