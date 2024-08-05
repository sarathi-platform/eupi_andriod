package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodEventDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodEventEntity
import com.sarathi.dataloadingmangement.model.response.LivelihoodEvent
import javax.inject.Inject

class LivelihoodEventRepositoryImpl @Inject constructor(
    private val livelihoodEventDao: LivelihoodEventDao,
    private val coreSharedPrefs: CoreSharedPrefs

) : ILivelihoodEventRepository {
    override suspend fun saveLivelihoodEventEntityToDB(livelihoodEvent: LivelihoodEvent) {
        livelihoodEventDao.insertLivelihood(
            LivelihoodEventEntity.getLivelihoodEventEntity(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                livelihoodEvent = livelihoodEvent
            )
        )
    }

}