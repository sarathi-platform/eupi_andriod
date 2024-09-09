package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import com.sarathi.dataloadingmangement.repository.liveihood.FetchSubjectLivelihoodEventHistoryRepository
import javax.inject.Inject

class FetchSubjectLivelihoodEventHistoryUseCase @Inject constructor(
    private val fetchSubjectLivelihoodEventHistoryRepository: FetchSubjectLivelihoodEventHistoryRepository
) {

    suspend operator fun invoke(subjectIds: List<Int>): Map<Int, Long> {
        return fetchSubjectLivelihoodEventHistoryRepository.getLastEventDateForSubjectLivelihoodEventMapping(
            subjectIds
        )
    }

}