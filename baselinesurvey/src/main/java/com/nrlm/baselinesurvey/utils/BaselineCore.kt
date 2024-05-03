package com.nrlm.baselinesurvey.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Network
import android.util.SparseArray
import androidx.compose.runtime.mutableStateOf
import androidx.core.util.forEach
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.download.AndroidDownloader
import com.nudge.communicationModule.EventObserverInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


object BaselineCore {

    private val TAG = BaselineCore::class.java.simpleName
    private lateinit var mainApplication: Application
    private lateinit var connectionLiveData: ConnectionMonitor
    private val validNetworksList: MutableSet<Network> = HashSet()

    private var downloader: AndroidDownloader? = null

    private var eventObservations = SparseArray<EventObserverInterface>()

    val autoReadOtp = mutableStateOf("")

    private var currentActivityName: String = BLANK_STRING

    private var referenceId: String = BLANK_STRING

    private var isEditAllowedForNoneMarkedQuestion: Boolean = true

    fun getCurrentActivityName() = currentActivityName

    fun setCurrentActivityName(activityName: String) {
        currentActivityName = activityName
    }

    fun getReferenceId() = referenceId

    fun setReferenceId(mReferenceId: String) {
        referenceId = mReferenceId
    }

    fun isEditAllowedForNoneMarkedQuestion() = isEditAllowedForNoneMarkedQuestion

    fun setIsEditAllowedForNoneMarkedQuestionFlag(flag: Boolean) {
        isEditAllowedForNoneMarkedQuestion = flag
    }


    fun init(context: Context) {
        downloader = AndroidDownloader(context)
        connectionLiveData = ConnectionMonitor(context)
        mainApplication= context as Application
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
                    BaselineLogger.e(tag, "appScopeLaunch $message", ex, true)
                }
            }
        } catch (ex: Throwable) {
            BaselineLogger.e(tag, message ?: "appScopeLaunch", ex)
        }
        return null
    }

    fun addCommunicationObserver(observer: EventObserverInterface, name: String) {
        val id = System.identityHashCode(observer)
        eventObservations.put(id, observer)
    }

    fun <T> notifyEventObservers(event: T) {
        eventObservations.forEach { id, observer ->
            observer.onEventCallback(event)
        }
    }


    fun getAndroidDownloader(): AndroidDownloader {
        return downloader ?: AndroidDownloader(mainApplication.applicationContext)
    }

    fun getConnectionMonitorLive(): ConnectionMonitor {
        return connectionLiveData
    }

    fun addValidNetworkToList(validNetwork: Network) {
        validNetworksList.add(validNetwork)
    }
    fun removeValidNetworkToList(validNetwork: Network) {
        validNetworksList.remove(validNetwork)
    }

    fun getValidNetworksList(): MutableSet<Network> {
        return validNetworksList
    }

    val isOnline = mutableStateOf(true)
    fun getAppContext(): Context {
        return mainApplication.applicationContext
    }

    fun startExternalApp(intent: Intent) {
        try {
            BaselineLogger.i(TAG, "startExternalApp() action: ${intent.action}")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            getAppContext().startActivity(intent)
        } catch (ex: Exception) {
            BaselineLogger.e(TAG, "startExternalActivity exception: ${ex.message}")
        }
    }

    fun cleanUp() {

    }

}