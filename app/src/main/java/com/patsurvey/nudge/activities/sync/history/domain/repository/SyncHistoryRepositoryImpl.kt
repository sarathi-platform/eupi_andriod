package com.patsurvey.nudge.activities.sync.history.domain.repository

import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.EventStatusEntity
import com.nudge.core.database.entities.Events
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

    override suspend fun getAllEventsBetweenDates(
        startDate: String,
        endDate: String
    ): List<EventStatusEntity> {
        return eventStatusDao.getAllEventStatusBetweenDates(
            mobileNumber = prefRepo.getMobileNumber()
        )
    }

    override suspend fun getAllEventsForUser(): List<Events> {
        return eventsDao.getAllEventsForUser(prefRepo.getMobileNumber())
    }

}