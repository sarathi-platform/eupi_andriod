package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.SubjectLivelihoodEventMappingDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.SubjectLivelihoodEventSummaryUiModel
import javax.inject.Inject

class SubjectLivelihoodEventMappingRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val subjectLivelihoodEventMappingDao: SubjectLivelihoodEventMappingDao
) : ISubjectLivelihoodEventMapping {

    override suspend fun getLivelihoodEventsWithAssetAndMoneyEntryForSubject(subjectId: Int): List<SubjectLivelihoodEventSummaryUiModel> {
        return subjectLivelihoodEventMappingDao.getLivelihoodEventsWithAssetAndMoneyEntryForSubject(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            subjectId = subjectId
        )
    }

    override suspend fun getSubjectLivelihoodEventMappingListFromDb(
        subjectId: Int
    ): List<SubjectLivelihoodEventMappingEntity>? {
        return subjectLivelihoodEventMappingDao.getSubjectLivelihoodEventMappingAvailable(
            subjectId = subjectId,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }

    override suspend fun addOrUpdateLivelihoodEvent(eventData: LivelihoodEventScreenData) {
        subjectLivelihoodEventMappingDao.insertSubjectLivelihoodEventMapping(
            subjectLivelihoodEventMappingEntity = SubjectLivelihoodEventMappingEntity.getSubjectLivelihoodEventMappingEntity(
                coreSharedPrefs.getUniqueUserIdentifier(), eventData
            )
        )
    }


}