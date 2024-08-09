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

    override suspend fun getSavedEventFromDb(
        subjectId: Int,
        transactionId: String
    ): SubjectLivelihoodEventMappingEntity? {
        return subjectLivelihoodEventMappingDao.getSubjectLivelihoodEventMappingAvailable(
            subjectId = subjectId,
            transactionId = transactionId,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }

    override suspend fun addOrUpdateLivelihoodEvent(eventData: LivelihoodEventScreenData) {
        val subjectLivelihoodEventMappingEntity =
            SubjectLivelihoodEventMappingEntity.getSubjectLivelihoodEventMappingEntity(
                coreSharedPrefs.getUniqueUserIdentifier(), eventData
            )
        if (subjectLivelihoodEventMappingDao.isLivelihoodEventMappingExist(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                transactionId = subjectLivelihoodEventMappingEntity.transactionId
            ) > 0
        ) {
            softDeleteLivelihoodEvent(
                subjectId = subjectLivelihoodEventMappingEntity.subjectId,
                transactionId = subjectLivelihoodEventMappingEntity.transactionId
            )
        }
        subjectLivelihoodEventMappingDao.insertSubjectLivelihoodEventMapping(
            subjectLivelihoodEventMappingEntity = subjectLivelihoodEventMappingEntity
        )


    }

    override suspend fun softDeleteLivelihoodEvent(
        transactionId: String,
        subjectId: Int
    ) {
        subjectLivelihoodEventMappingDao.softDeleteLivelihoodEventMapping(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            transactionId = transactionId,
            subjectId = subjectId
        )
    }


}