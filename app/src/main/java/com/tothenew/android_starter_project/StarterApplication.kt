package com.tothenew.android_starter_project

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StarterApplication: Application() {

    init {
        instance = this
    }
    companion object {
        lateinit var instance: StarterApplication

        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }
}