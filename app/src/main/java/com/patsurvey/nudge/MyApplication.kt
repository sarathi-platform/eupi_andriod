package com.patsurvey.nudge

import android.app.Application
import android.content.Context
import com.patsurvey.nudge.utils.NugdePrefs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {

    init {
        instance = this
        NugdePrefs.init(this)
    }
    companion object {
        lateinit var instance: MyApplication

        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }
}