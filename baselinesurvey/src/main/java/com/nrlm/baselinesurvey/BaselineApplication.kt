package com.nrlm.baselinesurvey

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.syncmanager.SyncManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltAndroidApp
class BaselineApplication: Application(), androidx.work.Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var eventsDao: EventsDao

    @Inject
    lateinit var eventDependencyDao: EventDependencyDao

    @Inject
    lateinit var syncManager: SyncManager

    override fun onCreate() {
        super.onCreate()
        init()
    }

    fun init() {
        instance = this
        BaselineCore.init(syncManager)

    }
    fun cleanUp() {
         BaselineCore.cleanUp(syncManager)
    }
    companion object {
        private val TAG = BaselineApplication::class.java.simpleName

        lateinit var instance: BaselineApplication

        fun applicationContext(): Context {
            return instance.applicationContext
        }

        private val applicationScope = CoroutineScope(SupervisorJob())
        private fun scope(): CoroutineScope {
            return applicationScope
        }

        fun appScopeLaunch(coroutineContext: CoroutineContext = Dispatchers.Default, tag: String = TAG, message: String? = "appScopeLaunch", func: suspend () -> Unit): Job? {
            try {
                return scope().launch(coroutineContext) {
                    try {
                        func()
                    } catch (ex: Throwable) {
//                        NudgeLogger.e(tag, "appScopeLaunch $message", ex, true)
                        Log.e(tag, "appScopeLaunch $message", ex)
                    }
                }
            } catch (ex: Throwable) {
//                NudgeLogger.e(tag, message ?: "appScopeLaunch", ex)
                Log.e(tag, message ?: "appScopeLaunch", ex)
            }
            return null
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

    override val workManagerConfiguration: androidx.work.Configuration
        get() =  androidx.work.Configuration.Builder()
            .setWorkerFactory(workerFactory).build()
}