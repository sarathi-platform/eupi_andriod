package com.sarathi.dataloadingmangement.domain.use_case.livlihood

import com.sarathi.dataloadingmangement.data.entities.SubjectEntity
import com.sarathi.dataloadingmangement.repository.livelihood.FetchDidiDetailsFromDbRepository
import javax.inject.Inject

class FetchDidiDetailsFromDbUseCase @Inject constructor(
    private val fetchDidiDetailsFromDbRepository: FetchDidiDetailsFromDbRepository
) {

    suspend operator fun invoke(): List<SubjectEntity> {
        val uniqueUserId = fetchDidiDetailsFromDbRepository.getUniqueUserId()
        return fetchDidiDetailsFromDbRepository.getSubjectListForUser(userId = uniqueUserId)
    }

}
