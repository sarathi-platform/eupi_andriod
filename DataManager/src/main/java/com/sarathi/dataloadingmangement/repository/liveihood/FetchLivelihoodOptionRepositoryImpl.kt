package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.data.dao.livelihood.SubjectLivelihoodMappingDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.domain.use_case.livelihood.LivelihoodOptionResponse
import com.sarathi.dataloadingmangement.enums.LivelihoodLanguageReferenceType
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject

class FetchLivelihoodOptionRepositoryImpl @Inject constructor(
    val coreSharedPrefs: CoreSharedPrefs,
    val apiInterface: DataLoadingApiService,
    private val activityConfigDao: ActivityConfigDao,
    private val subjectLivelihoodMappingDao: SubjectLivelihoodMappingDao,

    ) : FetchLivelihoodOptionRepository {
    override suspend fun getLivelihoodOptionNetwork(activityId: Int): ApiResponseModel<List<LivelihoodOptionResponse>> {
        return apiInterface.fetchLivelihoodPlanData(activityId)
    }

    override suspend fun saveAllSubjectLivelihoodDetails(subjectLivelihoodMappingEntity: List<SubjectLivelihoodMappingEntity>) {
        subjectLivelihoodMappingDao.insertAllSubjectLivelihoodMapping(subjectLivelihoodMappingEntity)
    }

    override suspend fun getActivityIdForLivelihood(): Int {
        return activityConfigDao.getActivityIdForLivelihood(
            activityType = LivelihoodLanguageReferenceType.Livelihood.name,
            coreSharedPrefs.getUniqueUserIdentifier()
        )
    }


}