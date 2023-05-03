package com.patsurvey.nudge

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate
import com.patsurvey.nudge.database.NudgeDatabase
import dagger.hilt.android.HiltAndroidApp
import java.util.*

@HiltAndroidApp
class MyApplication: Application() {

    init {
        instance = this
    }
    companion object {
        lateinit var instance: MyApplication

        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }
    private val localizationDelegate = LocalizationApplicationDelegate()

    override fun attachBaseContext(base: Context) {
        localizationDelegate.setDefaultLanguage(base, Locale.ENGLISH)
        super.attachBaseContext(localizationDelegate.attachBaseContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localizationDelegate.onConfigurationChanged(this)
    }

    override fun getApplicationContext(): Context {
        return localizationDelegate.getApplicationContext(super.getApplicationContext())
    }

    override fun getResources(): Resources {
        return localizationDelegate.getResources(baseContext, super.getResources())
    }
}