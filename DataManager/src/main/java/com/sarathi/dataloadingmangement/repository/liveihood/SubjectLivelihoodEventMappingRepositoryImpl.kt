package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.SubjectLivelihoodEventMappingDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity
import com.sarathi.dataloadingmangement.model.events.incomeExpense.SaveLivelihoodEventDto
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

    override suspend fun getDeletedLivelihoodEventsWithAssetAndMoneyEntry(subjectId: Int): List<SubjectLivelihoodEventSummaryUiModel> {
        return subjectLivelihoodEventMappingDao.getDeletedLivelihoodEventsWithAssetAndMoneyEntry(
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

    override suspend fun addOrUpdateLivelihoodEvent(
        eventData: LivelihoodEventScreenData,
        currentDateTime: Long,
        modifiedDateTime: Long
    ) {
        val subjectLivelihoodEventMappingEntity =
            SubjectLivelihoodEventMappingEntity.getSubjectLivelihoodEventMappingEntity(
                coreSharedPrefs.getUniqueUserIdentifier(),
                eventData,
                createdDate = currentDateTime,
                modifiedDate = modifiedDateTime,
                status = 1
            )
        if (subjectLivelihoodEventMappingDao.isLivelihoodEventMappingExist(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                transactionId = subjectLivelihoodEventMappingEntity.transactionId
            ) > 0
        ) {
            softDeleteLivelihoodEvent(
                transactionId = subjectLivelihoodEventMappingEntity.transactionId,
                subjectId = subjectLivelihoodEventMappingEntity.subjectId
            )
        }
        subjectLivelihoodEventMappingDao.insertSubjectLivelihoodEventMapping(
            subjectLivelihoodEventMappingEntity = subjectLivelihoodEventMappingEntity
        )


    }

    override suspend fun softDeleteLivelihoodEvent(
        transactionId: String,
        subjectId: Int,
        modifiedDateTime: Long
    ) {
        subjectLivelihoodEventMappingDao.softDeleteLivelihoodEventMapping(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            transactionId = transactionId,
            subjectId = subjectId,
            modifiedDate = modifiedDateTime
        )
    }

    override suspend fun softDeleteLivelihoodEvent(transactionId: String, subjectId: Int) {
        subjectLivelihoodEventMappingDao.softDeleteLivelihoodEventMapping(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            transactionId = transactionId,
            subjectId = subjectId
        )
    }

    override suspend fun getLivelihoodEventDto(
        eventData: LivelihoodEventScreenData,
        currentDateTime: Long,
        modifiedDateTime: Long
    ): SaveLivelihoodEventDto {
        return SaveLivelihoodEventDto(
            subjectId = eventData.subjectId,
            eventId = eventData.eventId,
            livelihoodId = eventData.livelihoodId,
            eventValue = eventData.eventValue,
            productId = eventData.productId,
            assetType = eventData.assetType,
            assetCount = eventData.assetCount,
            amount = eventData.amount,
            transactionId = eventData.transactionId,
            date = eventData.date,
            livelihoodValue = eventData.livelihoodValue,
            assetTypeValue = eventData.assetTypeValue,
            productValue = eventData.productValue,
            createdDate = currentDateTime,
            modifiedDate = modifiedDateTime,
            status = 1,
            eventType = eventData.selectedEvent.name
        )
    }

    override suspend fun getUserId(): Int {
        return coreSharedPrefs.getUserName().toInt()
    }

    override suspend fun getSubjectLivelihoodEventMappingListForTransactionIdFromDb(transactionId: String): List<SubjectLivelihoodEventMappingEntity>? {
        return subjectLivelihoodEventMappingDao.getSubjectLivelihoodEventMappingListForTransactionIdFromDb(
            userId = coreSharedPrefs.getUniqueUserIdentifier(), transactionId = transactionId
        )
    }

    override suspend fun getLivelihoodEventForUser(): List<SubjectLivelihoodEventMappingEntity> {
        return subjectLivelihoodEventMappingDao.getSubjectLivelihoodEventMappingForUser(
            coreSharedPrefs.getUniqueUserIdentifier()
        )
    }


}