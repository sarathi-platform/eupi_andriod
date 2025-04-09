package com.patsurvey.nudge.activities

import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import android.os.storage.StorageManager
import android.text.format.Formatter
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.install.model.AppUpdateType
import com.nrlm.baselinesurvey.data.domain.useCase.UpdateBaselineStatusOnInitUseCase
import com.nudge.core.analytics.AnalyticsManager
import com.nudge.core.analytics.mixpanel.AnalyticsEvents
import com.nudge.core.analytics.mixpanel.AnalyticsEventsParam
import com.nudge.core.database.dao.CasteListDao
import com.nudge.core.database.dao.language.LanguageListDao
import com.nudge.core.enums.AppConfigKeysEnum
import com.nudge.core.enums.SyncAlertType
import com.nudge.core.model.EventLimitAlertUiModel
import com.nudge.core.notifications.NotificationHandler
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.ui.events.CommonEvents
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.activities.domain.useCase.CheckEventLimitThresholdUseCase
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.BpcSummaryDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.PoorDidiListDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.database.dao.TrainingVideoDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.ConnectionMonitorV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val coreSharedPrefs: CoreSharedPrefs,
    val apiService: ApiService,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val villegeListDao: VillageListDao,
    val didiDao: DidiDao,
    val answerDao: AnswerDao,
    val numericAnswerDao: NumericAnswerDao,
    val questionDao: QuestionListDao,
    val casteListDao: CasteListDao,
    val trainingVideoDao: TrainingVideoDao,
    val bpcSummaryDao: BpcSummaryDao,
    val poorDidiListDao: PoorDidiListDao,
    val languageListDao: LanguageListDao,
    val connectionMonitor: ConnectionMonitorV2,
    val updateBaselineStatusOnInitUseCase: UpdateBaselineStatusOnInitUseCase,
    val checkEventLimitThresholdUseCase: CheckEventLimitThresholdUseCase,
    val notificationHandler: NotificationHandler,
    val analyticsManager: AnalyticsManager
): BaseViewModel() {
    val isLoggedIn = mutableStateOf(false)
    val isOnline = connectionMonitor.isConnected.asLiveData()
    val isInAppUpdate = mutableStateOf(false)
    val appUpdateType = mutableStateOf(AppUpdateType.IMMEDIATE)
    val showUpdateDialog = mutableStateOf(false)
    val isAppLinkOpen = mutableStateOf(false)


    init {
        prefRepo.saveBuildEnvironment(BuildConfig.FLAVOR.uppercase(Locale.ENGLISH))
    }

    fun isLoggedIn() = (prefRepo.getAccessToken()?.isNotEmpty() == true)
    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        //TODO("Not yet implemented")
    }


    fun saveSyncEnabledFromRemoteConfig(isEnabled: Boolean) {
        prefRepo.saveIsSyncEnabled(isEnabled)
    }

    fun saveSyncBatchSizeFromRemoteConfig(batchSize: Long) {
        prefRepo.saveSyncBatchSize(batchSize)
    }

    fun saveSyncRetryCountFromRemoteConfig(retryCount: Long) {
        prefRepo.saveSyncRetryCount(retryCount)
    }
    fun saveSyncOptionEnablesFromRemoteConfig(isSyncOptionEnable: Boolean) {
        prefRepo.setSyncOptionEnabled(isSyncOptionEnable)
    }


    fun saveDataTabVisibility(isEnabled: Boolean) {
        prefRepo.saveDataTabVisibility(isEnabled)
    }

    fun saveMixPanelToken(token: String) {
        coreSharedPrefs.saveMixPanelToken(token)
    }

    fun saveSyncImageBlobUploadEnable(isBlobUploadEnable: Boolean) {
        coreSharedPrefs.saveSyncImageBlobUploadEnable(isBlobUploadEnable)
    }

    fun updateBaselineStatusOnInit(onSuccess: (isSuccess: Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            updateBaselineStatusOnInitUseCase.invoke() {
                onSuccess(it)
            }
        }

    }

    override fun <T> onEvent(event: T) {
        super.onEvent(event)
        if (event is CommonEvents.CheckEventLimitThreshold) {
            if (isLoggedIn()) {
                viewModelScope.launch(Dispatchers.IO) {
                    val result = checkEventLimitThresholdUseCase.invoke()
                    withContext(Dispatchers.Main) {
                        event.result(result)
                    }
                }
            } else {
                event.result(SyncAlertType.NO_ALERT)
            }
        }
    }

    fun showSoftLimitAlert(title: String, message: String): EventLimitAlertUiModel {
        return EventLimitAlertUiModel.getDefaultEventLimitAlertUiModel(
            alertTitle = title,
            alertMessage = message
        )
    }

    fun showHardLimitAlert(title: String, message: String): EventLimitAlertUiModel {
        return EventLimitAlertUiModel.getHardEventLimitAlertUiModel(
            alertTitle = title,
            alertMessage = message
        )
    }

    fun setV2TheameEnable() {
        coreSharedPrefs.savePref(
            AppConfigKeysEnum.V2TheameEnable.name,
            true
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendAppOpenEvent(context: Context) {
        val params = mutableMapOf<String, String>()
        params.putAll(getStorageParams(context))
        params[AnalyticsEventsParam.SELECTED_LANGUAGE.eventParam] = coreSharedPrefs.getAppLanguage()
        analyticsManager.trackEvent(AnalyticsEvents.APP_LAUNCHED.eventName, params)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStorageParams(context: Context): Map<String, String> {
        val params = mutableMapOf<String, String>()
        val storageStatsManager =
            context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager

        if (storageStatsManager == null || storageManager == null)
            return emptyMap()

        val storageVolumes = storageManager.storageVolumes
        for (storageVolume in storageVolumes) {

            val uuidStr = storageVolume.uuid
            val uuid =
                if (uuidStr == null) StorageManager.UUID_DEFAULT else UUID.fromString(uuidStr)

            try {
                val totalStorage =
                    Formatter.formatShortFileSize(context, storageStatsManager.getTotalBytes(uuid))
                params[AnalyticsEventsParam.TOTAL_STORAGE.eventParam] = totalStorage

                val freeSpace =
                    Formatter.formatShortFileSize(context, storageStatsManager.getFreeBytes(uuid))
                params[AnalyticsEventsParam.FREE_STORAGE.eventParam] = freeSpace

                val storageUsedByApp = getStorageUsedByApp(context, uuid, storageStatsManager)
                params.putAll(storageUsedByApp)

            } catch (ex: Exception) {
                // ignore
            }
        }
        return params
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStorageUsedByApp(
        context: Context,
        uuid: UUID,
        storageStatsManager: StorageStatsManager
    ): Map<String, String> {
        val params = mutableMapOf<String, String>()

        val user = Process.myUserHandle()

        val storageStats = storageStatsManager.queryStatsForPackage(uuid, context.packageName, user)

        params[AnalyticsEventsParam.APP_SIZE.eventParam] =
            Formatter.formatShortFileSize(context, storageStats.appBytes)
        params[AnalyticsEventsParam.DATA_SIZE.eventParam] =
            Formatter.formatShortFileSize(context, storageStats.dataBytes)
        params[AnalyticsEventsParam.CACHE_SIZE.eventParam] =
            Formatter.formatShortFileSize(context, storageStats.cacheBytes)

        return params
    }
}