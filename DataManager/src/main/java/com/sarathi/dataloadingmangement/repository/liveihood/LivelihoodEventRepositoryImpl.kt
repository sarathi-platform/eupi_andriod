package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodEventDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodEventEntity
import com.sarathi.dataloadingmangement.model.response.LivelihoodEvent
import javax.inject.Inject

class LivelihoodEventRepositoryImpl @Inject constructor(
    private val livelihoodEventDao: LivelihoodEventDao

) : ILivelihoodEventRepository {
    override suspend fun saveLivelihoodEventEntityToDB(livelihoodEvent: LivelihoodEvent) {
        livelihoodEventDao.insertLivelihood(
            LivelihoodEventEntity.getLivelihoodEventEntity(
                userId = "",
                livelihoodEvent = livelihoodEvent
            )
        )
    }

}