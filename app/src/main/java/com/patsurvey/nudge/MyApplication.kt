package com.patsurvey.nudge

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Network
import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate
import com.patsurvey.nudge.utils.NudgeLogger
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.coroutines.CoroutineContext

@HiltAndroidApp
class MyApplication: Application() {

    init {
        instance = this
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