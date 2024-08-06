package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.SubjectEntityWithLivelihoodMappingUiModel
import javax.inject.Inject

class FetchDidiDetailsWithLivelihoodMappingRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val subjectEntityDao: SubjectEntityDao,
) : FetchDidiDetailsWithLivelihoodMappingRepository {

    override suspend fun fetchDidiDetailsWithLivelihoodMapping(): List<SubjectEntityWithLivelihoodMappingUiModel> {
        return emptyList()
//        subjectEntityDao.getSubjectEntityWithLivelihoodMappingUiModelList(userId = coreSharedPrefs.getUniqueUserIdentifier())
    }


}