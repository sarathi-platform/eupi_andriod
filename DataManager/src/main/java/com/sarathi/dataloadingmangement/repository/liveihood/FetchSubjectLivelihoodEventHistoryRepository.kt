package com.sarathi.dataloadingmangement.repository.liveihood

interface FetchSubjectLivelihoodEventHistoryRepository {
    suspend fun getLastEventDateForSubjectLivelihoodEventMapping(subjectIds: List<Int>): Map<Int, Long>
}