package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.SubjectLivelihoodEventMappingDao
import javax.inject.Inject

class FetchSubjectLivelihoodEventHistoryRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val subjectLivelihoodEventMappingDao: SubjectLivelihoodEventMappingDao
) : FetchSubjectLivelihoodEventHistoryRepository {

    override suspend fun getLastEventDateForSubjectLivelihoodEventMapping(subjectIds: List<Int>): Map<Int, Long> {
        val subjectEventHistoryMap: MutableMap<Int, Long> = HashMap()
        val userId = coreSharedPrefs.getUniqueUserIdentifier()
        subjectIds.forEach { id ->
            subjectLivelihoodEventMappingDao.getLastEventDateForSubjectLivelihoodEventMapping(
                userId,
                id
            )?.let { lastEventDate ->
                subjectEventHistoryMap.put(id, lastEventDate)
            }
        }

        return subjectEventHistoryMap
    }

}