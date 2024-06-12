package com.patsurvey.nudge.activities.sync.history.domain.repository

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_KEY_EMAIL
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.EventStatusEntity
import com.patsurvey.nudge.data.prefs.PrefRepo
import javax.inject.Inject

class SyncHistoryRepositoryImpl @Inject constructor(
    val prefRepo: PrefRepo,
    val eventsDao: EventsDao,
    val eventStatusDao: EventStatusDao
):SyncHistoryRepository{
    override fun getUserMobileNumber(): String {
        return prefRepo.getMobileNumber()
    }

    override fun getUserID(): String {
        return prefRepo.getUserId()
    }

    override fun getAllEventsBetweenDates(startDate:String,endDate:String): List<EventStatusEntity> {
        return eventStatusDao.getAllEventStatusBetweenDates(
            startDate = startDate,
            endDate = endDate,
            mobileNumber = prefRepo.getMobileNumber()
        )
    }

    override fun getAllEventStatusForUser(): List<EventStatusEntity> {
        return eventStatusDao.getAllEventStatusForUser(prefRepo.getMobileNumber())
    }

}