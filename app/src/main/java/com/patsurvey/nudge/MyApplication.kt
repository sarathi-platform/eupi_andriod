package com.patsurvey.nudge

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Network
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate
import com.nudge.core.Core
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.syncmanager.SyncManager
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.NudgeLogger.e
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltAndroidApp
class MyApplication : Application(), androidx.work.Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var eventsDao: EventsDao

    @Inject
    lateinit var eventDependencyDao: EventDependencyDao

    @Inject
    lateinit var syncManager: SyncManager


    private fun init() {
        instance = this
        NudgeCore.initEventObserver(syncManager)
        BaselineCore.init(instance.applicationContext)
        Core().init(instance.applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        init()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onAppDestroy() {
        NudgeLogger.d(TAG, "onAppDestroy")
        cleanUp()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        NudgeLogger.d(TAG, "onAppForegrounded")
//        CompatibilityUtil.setAppBackground(false)
    }

    private fun cleanUp() {
        NudgeCore.cleanUp(syncManager)
    }


    companion object {
        private val TAG = MyApplication::class.java.simpleName

        lateinit var instance: MyApplication

        private val validNetworksList: MutableSet<Network> = HashSet()

        fun addValidNetworkToList(validNetwork: Network) {
            validNetworksList.add(validNetwork)
        }
        fun removeValidNetworkToList(validNetwork: Network) {
            validNetworksList.remove(validNetwork)
        }

        fun getValidNetworksList(): MutableSet<Network> {
            return validNetworksList
        }
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
                        NudgeLogger.e(tag, "appScopeLaunch $message", ex, true)
                    }
                }
            } catch (ex: Throwable) {
                NudgeLogger.e(tag, message ?: "appScopeLaunch", ex)
            }
            return null
        }

        fun appScopeAsync(coroutineContext: CoroutineContext = Dispatchers.Default, tag: String = TAG, message: String? = "appScopeAsync", func: suspend () -> Unit): Deferred<Unit>? {
            try {
                val asyncJob = scope().async(coroutineContext) {
                    try {
                        func()
                    } catch (ex: Throwable) {
                        e(tag, "appScopeAsync $message", ex, true)
                    }
                }
                return asyncJob
            } catch (ex: Throwable) {
                e(tag, message ?: "appScopeAsync", ex)
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

    override fun getWorkManagerConfiguration(): androidx.work.Configuration {
        return androidx.work.Configuration.Builder()
            .setWorkerFactory(workerFactory).build()
    }
}