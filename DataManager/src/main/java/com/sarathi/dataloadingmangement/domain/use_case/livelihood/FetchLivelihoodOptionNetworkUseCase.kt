package com.sarathi.dataloadingmangement.domain.use_case.livelihood

import com.nudge.core.BLANK_STRING
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.CoreLogger
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.network.SUBPATH_FETCH_LIVELIHOOD_OPTION
import com.sarathi.dataloadingmangement.repository.liveihood.FetchLivelihoodOptionRepository
import javax.inject.Inject


class FetchLivelihoodOptionNetworkUseCase @Inject constructor(
    private val repository: FetchLivelihoodOptionRepository,
    private val coreSharedPrefs: CoreSharedPrefs,
) {

    private suspend fun getSubjectLivelihoodMapping (activityId: Int): Boolean {
        val startTime = System.currentTimeMillis()

        val apiResponse = repository.getLivelihoodOptionNetwork(
            activityId = activityId,
        )
        CoreLogger.d(
            tag = "LazyLoadAnalysis",
            msg = "getSubjectLivelihoodMapping :$SUBPATH_FETCH_LIVELIHOOD_OPTION/activityId=${activityId}  : ${System.currentTimeMillis() - startTime}"
        )
        if (apiResponse.status.equals(SUCCESS_CODE, true) || apiResponse.status.equals(
                SUCCESS,
                true
            )
        ) {
            val subjectLivelihoodMappingEntities = mutableListOf<SubjectLivelihoodMappingEntity>()
            apiResponse.data?.let { subjectLivelihoodMappingDetail ->
                subjectLivelihoodMappingDetail.forEach { subjectLivelihoodMapping ->

                    subjectLivelihoodMapping.livelihoodDTO.forEach {
                        subjectLivelihoodMappingEntities.add(
                            SubjectLivelihoodMappingEntity.getSubjectLivelihoodMappingEntity(
                                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                                subjectId = subjectLivelihoodMapping.didiId,
                                livelihoodId = it.programLivelihoodId,
                                status = 1,
                                type = it.order,
                                livelihoodType = it.type ?: BLANK_STRING
                            )
                        )
                    }
                }
                repository.saveAllSubjectLivelihoodDetails(subjectLivelihoodMappingEntities)
                CoreLogger.d(
                    tag = "LazyLoadAnalysis",
                    msg = "save getSubjectLivelihoodMapping : ${System.currentTimeMillis() - startTime}"
                )

                return true
            }
        } else {
            CoreLogger.d(
                tag = "LazyLoadAnalysis",
                msg = "save getSubjectLivelihoodMapping : ${System.currentTimeMillis() - startTime}"
            )

            return true
        }
        return false
    }

    suspend fun invoke(): Boolean {
        try {

            if (!repository.isLivelihoodAlreadyFetched()) {
                var getActivityIdForLivelihood = repository.getActivityIdForLivelihood()
                getSubjectLivelihoodMapping(
                    activityId = getActivityIdForLivelihood,
                )
            }
        } catch (ex: Exception) {
            throw ex
        }
        return true
    }
}
