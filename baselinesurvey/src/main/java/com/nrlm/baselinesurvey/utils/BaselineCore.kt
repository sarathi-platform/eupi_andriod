package com.nrlm.baselinesurvey.utils

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Network
import android.util.Log
import android.util.SparseArray
import androidx.compose.runtime.mutableStateOf
import androidx.core.util.forEach
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.BaselineApplication
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.download.AndroidDownloader
import com.nrlm.baselinesurvey.download.utils.FileType
import com.nudge.communicationModule.EventObserverInterface

object BaselineCore {

    private val TAG = BaselineCore::class.java.simpleName

    private lateinit var connectionLiveData: ConnectionMonitor
    private val validNetworksList: MutableSet<Network> = HashSet()

    private var downloader: AndroidDownloader? = null

    private var eventObservations = SparseArray<EventObserverInterface>()

    val autoReadOtp = mutableStateOf("")

    private var currentActivityName: String = BLANK_STRING

    fun getCurrentActivityName() = currentActivityName

    fun setCurrentActivityName(activityName: String) {
        currentActivityName = activityName
    }


    fun init() {
        downloader = AndroidDownloader(BaselineApplication.applicationContext())
        connectionLiveData = ConnectionMonitor(BaselineApplication.applicationContext())
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
        return downloader ?: AndroidDownloader(BaselineApplication.applicationContext())
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
        return BaselineApplication.applicationContext()
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

    fun downloadQuestionImages(questionImageLinks: List<String>) {
        val context = BaselineApplication.applicationContext()
        BaselineApplication.appScopeLaunch {
            try {
                questionImageLinks.forEach { questionImageLink ->
                    if (!getImagePath(context, questionImageLink).exists()) {
                        val localDownloader = getAndroidDownloader()
                        val downloadManager = context.getSystemService(DownloadManager::class.java)
                        val downloadId = localDownloader?.downloadImageFile(questionImageLink, FileType.IMAGE)
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("VideoListViewModel", "downloadItem exception", ex)
            }
        }
    }

    fun downloadAuthorizedImageItem(id:Int, image: String, prefRepo: PrefRepo, surveyeeEntityDao: SurveyeeEntityDao) {
        BaselineApplication.appScopeLaunch {
            try {
                val imageFile = getAuthImagePath(getAppContext(), image)
                if (!imageFile.exists()) {
                    val localDownloader = downloader
                    val downloadManager = getAppContext().getSystemService(DownloadManager::class.java)
                    localDownloader?.currentDownloadingId?.value = id
                    val downloadId = localDownloader?.downloadAuthorizedImageFile(
                        image,
                        FileType.IMAGE,
                        prefRepo
                    )
                    if (downloadId != null) {
                        localDownloader.checkDownloadStatus(downloadId,
                            id,
                            downloadManager,
                            onDownloadComplete = {
                                surveyeeEntityDao.updateImageLocalPath(id,imageFile.absolutePath)
                            }, onDownloadFailed = {
                                BaselineLogger.d("VillageSelectorViewModel", "downloadAuthorizedImageItem -> onDownloadFailed")
                            })
                    }
                } else {
                    surveyeeEntityDao.updateImageLocalPath(id,imageFile.absolutePath)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                BaselineLogger.e("VillageSelectorViewModel", "downloadAuthorizedImageItem -> downloadItem exception", ex)
            }
        }
    }

    fun cleanUp() {

    }

}