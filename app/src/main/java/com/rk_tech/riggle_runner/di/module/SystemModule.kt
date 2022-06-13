package com.rk_tech.riggle_runner.di.module

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import com.rk_tech.riggle_runner.ui.base.connectivity.ConnectivityProvider
import com.rk_tech.riggle_runner.ui.base.connectivity.ConnectivityProviderImpl
import com.rk_tech.riggle_runner.ui.base.connectivity.ConnectivityProviderLegacyImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SystemModule {
    @Singleton
    @Provides
    fun networkHandler(@ApplicationContext context: Context): ConnectivityProvider {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityProviderImpl(cm)
        } else {
            ConnectivityProviderLegacyImpl(context, cm)
        }
    }

}