package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.model.events.LivelihoodPlanActivityEventDto
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.LivelihoodEventDao
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventUiModel
import javax.inject.Inject

class LivelihoodEventRepositoryImpl @Inject constructor(
    private val livelihoodEventDao: LivelihoodEventDao,
     private val coreSharedPrefs: CoreSharedPrefs) : ILivelihoodEventRepository {
    override fun getSaveLivelihoodEventDto(
        livelihoodEntity: LivelihoodPlanActivityEventDto
    ): LivelihoodPlanActivityEventDto {
        return LivelihoodPlanActivityEventDto(
            userId = livelihoodEntity.userId,
            primaryLivelihoodId = livelihoodEntity.primaryLivelihoodId,
            secondaryLivelihoodId = livelihoodEntity.secondaryLivelihoodId,
            activityId = livelihoodEntity.activityId,
            missionId = livelihoodEntity.missionId,
            subjectId = livelihoodEntity.subjectId,
            subjectType = livelihoodEntity.subjectType
        )
    }


    override suspend fun getEventsForLivelihood(livelihoodId: Int): List<LivelihoodEventUiModel> {
        return livelihoodEventDao.getEventsForLivelihood(
            livelihoodId = livelihoodId,
            languageCode = coreSharedPrefs.getAppLanguage(),
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }

    override suspend fun getEventsForLivelihood(livelihoodIds: List<Int>): List<LivelihoodEventUiModel> {
        return livelihoodEventDao.getEventsForLivelihood(
            livelihoodIds = livelihoodIds,
            languageCode = coreSharedPrefs.getAppLanguage(),
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }
}
