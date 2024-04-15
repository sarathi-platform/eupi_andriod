package com.nrlm.baselinesurvey.ui.setting.domain.repository

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.LANGUAGE_OPEN_FROM_SETTING
import com.nrlm.baselinesurvey.data.domain.EventWriterHelper
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.NudgeBaselineDatabase
import com.nrlm.baselinesurvey.model.response.ApiResponseModel
import com.nrlm.baselinesurvey.network.interfaces.ApiService
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nrlm.baselinesurvey.utils.states.SectionStatus.Companion.getSectionStatusNameFromOrdinal
import com.nudge.core.database.entities.Events
import com.nudge.core.getDefaultBackUpFileName
import com.nudge.core.getDefaultImageBackUpFileName
import com.nudge.core.preference.CoreSharedPrefs

class SettingBSRepositoryImpl(private val prefRepo: PrefRepo,
                              private val apiService: ApiService,
                              private val eventWriterHelper: EventWriterHelper,
                              private val baselineDatabase: NudgeBaselineDatabase
    ):SettingBSRepository {

    override suspend fun performLogout(): ApiResponseModel<String> {
        return apiService.performLogout()
    }

    override fun clearSharedPref() {
        prefRepo.saveAccessToken(BLANK_STRING)
        val coreSharedPrefs = CoreSharedPrefs.getInstance(BaselineCore.getAppContext())
        coreSharedPrefs.setBackupFileName(
            getDefaultBackUpFileName(
                prefRepo.getMobileNumber() ?: BLANK_STRING
            )
        )
        coreSharedPrefs.setImageBackupFileName(
            getDefaultImageBackUpFileName(
                prefRepo.getMobileNumber() ?: ""
            )
        )
        coreSharedPrefs.setFileExported(false)
        prefRepo.setPreviousUserMobile(prefRepo.getMobileNumber() ?: BLANK_STRING)

    }

    override fun saveLanguageScreenOpenFrom() {
        prefRepo.savePref(LANGUAGE_OPEN_FROM_SETTING,true)
    }

    override suspend fun regenerateAllBaselineEvent() {
        val events = ArrayList<Events>()
    }

    suspend fun regenerateMATStatusEvent(events: ArrayList<Events>) {
        baselineDatabase.missionEntityDao().getMissions().forEach { missionEntity ->
            events.add(
                eventWriterHelper.createMissionStatusUpdateEvent(
                    missionId = missionEntity.missionId,
                    SectionStatus.valueOf(getSectionStatusNameFromOrdinal(missionEntity.missionStatus))
                )
            )
        }
        // baselineDatabase.missionActivityEntityDao().getActivities()
    }


}