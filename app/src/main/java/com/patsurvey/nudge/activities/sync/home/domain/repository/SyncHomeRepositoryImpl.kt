package com.patsurvey.nudge.activities.sync.home.domain.repository

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_KEY_EMAIL
import com.nrlm.baselinesurvey.PREF_KEY_NAME
import com.nudge.core.EventSyncStatus
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.Events
import com.nudge.core.preference.CorePrefRepo
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.utils.CRP_USER_TYPE
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME

class SyncHomeRepositoryImpl(
    val prefRepo: PrefRepo,
    val corePrefRepo: CorePrefRepo,
    val eventsDao: EventsDao
):SyncHomeRepository {
    override fun getTotalEvents(): List<Events> {
        return eventsDao.getTotalEventCount(prefRepo.getMobileNumber())
    }
    override fun getUserMobileNumber(): String {
        return prefRepo.getMobileNumber()
    }

    override fun getUserID(): String {
        return prefRepo.getUserId()
    }

    override fun getUserEmail(): String {
        return prefRepo.getPref(PREF_KEY_EMAIL, BLANK_STRING)?: BLANK_STRING
    }

    override fun getUserName(): String {
        return prefRepo.getPref(PREF_KEY_NAME, BLANK_STRING) ?: BLANK_STRING
    }

    override fun getLoggedInUserType(): String {
        return prefRepo.getPref(PREF_KEY_TYPE_NAME, CRP_USER_TYPE) ?: CRP_USER_TYPE
    }

}