package com.programacustofrete.custofrete.data.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AppApplication)
            modules(dataSourceModule)
            modules(repositoryModule)
            modules(useCaseModule)
            modules(interactorModule)
            modules(viewModel)

        }
    }
}