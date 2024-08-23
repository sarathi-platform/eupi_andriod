package com.patsurvey.nudge.activities.sync.home.domain.repository

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_KEY_EMAIL
import com.nrlm.baselinesurvey.PREF_KEY_NAME
import com.nudge.core.EventSyncStatus
import com.nudge.core.LAST_SYNC_TIME
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.Events
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.response.LastSyncResponseModel
import com.nudge.core.preference.CorePrefRepo
import com.nudge.syncmanager.network.SyncApiService
import com.patsurvey.nudge.utils.CRP_USER_TYPE
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME

class SyncHomeRepositoryImpl(
    val corePrefRepo: CorePrefRepo,
    val eventsDao: EventsDao,
    val syncApiService: SyncApiService
):SyncHomeRepository {
    override fun getTotalEvents(): LiveData<List<Events>>{
        return eventsDao.getTotalSyncEvent(corePrefRepo.getMobileNo())
    }

    override fun getTotalEventCount(): Int {
        return eventsDao.getTotalSyncEventCount(corePrefRepo.getMobileNo())
    }

    override fun getAllFailedEventListFromDB(
    ): List<Events> {
        return eventsDao.fetchAllFailedEventList(
            mobileNumber = corePrefRepo.getMobileNo(),
            status = listOf(
                EventSyncStatus.PRODUCER_FAILED.eventSyncStatus,
                EventSyncStatus.CONSUMER_IN_PROGRESS.eventSyncStatus,
                EventSyncStatus.CONSUMER_FAILED.eventSyncStatus
            )
        )
    }


    override fun getUserMobileNumber(): String {
        return corePrefRepo.getMobileNo()
    }

    override fun getUserID(): String {
        return corePrefRepo.getUserId()
    }

    override fun getUserEmail(): String {
        return corePrefRepo.getPref(PREF_KEY_EMAIL, BLANK_STRING) ?: BLANK_STRING
    }

    override fun getUserName(): String {
        return corePrefRepo.getPref(PREF_KEY_NAME, BLANK_STRING) ?: BLANK_STRING
    }

    override fun getLoggedInUserType(): String {
        return corePrefRepo.getPref(PREF_KEY_TYPE_NAME, CRP_USER_TYPE) ?: CRP_USER_TYPE
    }

    override fun saveLastSyncDateTime(dateTime: Long) {
        return corePrefRepo.savePref(LAST_SYNC_TIME, dateTime)
    }

    override suspend fun getLastSyncDateTime(): ApiResponseModel<LastSyncResponseModel> {
        return syncApiService.fetchLastSyncStatus(corePrefRepo.getMobileNo())

    }

}