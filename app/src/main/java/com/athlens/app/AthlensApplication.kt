package com.athlens.app

import android.app.Application
import com.athlens.app.di.AppContainer
import com.athlens.app.di.DefaultAppContainer

class AthlensApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
