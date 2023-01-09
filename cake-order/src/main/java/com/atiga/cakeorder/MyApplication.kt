package com.atiga.cakeorder

import android.app.Application
import com.atiga.cakeorder.core.di.networkModule
import com.atiga.cakeorder.core.di.repositoryModule
import com.atiga.cakeorder.di.useCaseModule
import com.atiga.cakeorder.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MyApplication)
            modules(
                listOf(
                    networkModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule
                )
            )
        }
    }
}