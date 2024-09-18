package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.dao.livelihood.SubjectLivelihoodEventMappingDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity
import com.sarathi.dataloadingmangement.enums.LivelihoodEventTypeDataCaptureMapping.Companion.getLivelihoodEventFromName
import com.sarathi.dataloadingmangement.model.response.LivelihoodSaveEventResponse
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject

class GetLivelihoodSaveEventRepositoryImpl @Inject constructor(
    private val sharedPrefs: CoreSharedPrefs,
    private val apiInterface: DataLoadingApiService,
    private val subjectLivelihoodEventMappingDao: SubjectLivelihoodEventMappingDao
) : IGetLivelihoodSaveEventRepository {
    override suspend fun getLivelihoodSaveEventFromNetwork(): ApiResponseModel<List<LivelihoodSaveEventResponse>> {
        return apiInterface.getSaveLivelihoodEvent(sharedPrefs.getUserNameInInt())
    }

    override suspend fun saveLivelihoodSaveEventIntoDb(livelihoodSaveEventResponse: List<LivelihoodSaveEventResponse>) {
        val subjectLivelihoodEventMappingEntity = ArrayList<SubjectLivelihoodEventMappingEntity>()
        livelihoodSaveEventResponse.forEach {
            subjectLivelihoodEventMappingEntity.add(
                SubjectLivelihoodEventMappingEntity.getSubjectLivelihoodEventMappingEntity(
                    uniqueUserIdentifier = sharedPrefs.getUniqueUserIdentifier(),
                    eventData = LivelihoodEventScreenData(
                        subjectId = it.subjectId,
                        amount = it.amount,
                        date = it.date,
                        assetCount = it.assetCount,
                        livelihoodId = it.livelihoodId,
                        productValue = it.productValue ?: BLANK_STRING,
                        assetTypeValue = it.assetTypeValue ?: BLANK_STRING,
                        eventId = it.eventId,
                        eventValue = it.eventValue ?: BLANK_STRING,
                        productId = it.productId,
                        assetType = it.assetType,
                        transactionId = it.transactionId,
                        selectedEvent = getLivelihoodEventFromName(it.eventType ?: BLANK_STRING),
                        livelihoodValue = it.livelihoodValue ?: BLANK_STRING
                    ),
                    createdDate = it.createdDate,
                    modifiedDate = it.modifiedDate

                )
            )
        }
        subjectLivelihoodEventMappingDao.insertSubjectLivelihoodEventMapping(
            subjectLivelihoodEventMappingEntity
        )
    }

}