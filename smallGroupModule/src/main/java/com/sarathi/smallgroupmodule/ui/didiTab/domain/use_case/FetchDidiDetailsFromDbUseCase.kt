package com.sarathi.smallgroupmodule.ui.didiTab.domain.use_case

import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.smallgroupmodule.ui.didiTab.domain.repository.FetchDidiDetailsFromDbRepository
import javax.inject.Inject

class FetchDidiDetailsFromDbUseCase @Inject constructor(
    private val fetchDidiDetailsFromDbRepository: FetchDidiDetailsFromDbRepository
) {

    suspend operator fun invoke(): List<SubjectEntity> {
        val uniqueUserId = fetchDidiDetailsFromDbRepository.getUniqueUserId()
        return fetchDidiDetailsFromDbRepository.getSubjectListForUser(userId = uniqueUserId)
    }

}
