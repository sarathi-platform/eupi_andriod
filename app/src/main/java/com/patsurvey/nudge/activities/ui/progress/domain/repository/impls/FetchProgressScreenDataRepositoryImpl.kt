package com.patsurvey.nudge.activities.ui.progress.domain.repository.impls

import com.nudge.core.preference.CoreSharedPrefs
import com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces.FetchProgressScreenDataRepository
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.EMPTY_TOLA_NAME
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.WealthRank
import javax.inject.Inject

class FetchProgressScreenDataRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val stepsListDao: StepsListDao,
    private val didiDao: DidiDao,
    private val tolaDao: TolaDao
) : FetchProgressScreenDataRepository {

    override suspend fun getStepListForVillage(villageId: Int): List<StepListEntity> {
        return stepsListDao.getAllStepsForVillage(villageId)
    }

    override suspend fun getStepSummaryForVillage(villageId: Int): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        val tolaList = tolaDao.getAllTolasForVillage(villageId)
        val didiList = didiDao.getAllDidisForVillage(villageId)

        StepNameEnum.values().forEach {
            val finalCount = when (it) {
                StepNameEnum.TRANSECT_WALK -> {
                    tolaList.filter { it.name != EMPTY_TOLA_NAME }.size
                }

                StepNameEnum.SOCIAL_MAPPING -> {
                    didiList.filter { it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal }.size
                }

                StepNameEnum.WEALTH_RANKING -> {
                    didiList.filter { it.wealth_ranking == WealthRank.POOR.rank && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal }.size
                }

                StepNameEnum.PAT_SURVEY -> {
                    didiList.filter {
                        it.forVoEndorsement == 1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal
                                && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal
                    }.size
                }

                StepNameEnum.VO_ENDORSEMENT -> {
                    didiList.filter {
                        it.forVoEndorsement == 1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal
                                && it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal
                    }.size
                }
            }
            map[it.name] = finalCount
        }

        return map
    }


}

enum class StepNameEnum(orderNumber: Int) {

    TRANSECT_WALK(1),
    SOCIAL_MAPPING(2),
    WEALTH_RANKING(3),
    PAT_SURVEY(4),
    VO_ENDORSEMENT(5);

    companion object {

        fun getStepNameForOrderNumber(orderNumber: Int): StepNameEnum? {

            return when (orderNumber) {
                1 -> TRANSECT_WALK
                2 -> SOCIAL_MAPPING
                3 -> WEALTH_RANKING
                4 -> PAT_SURVEY
                5 -> VO_ENDORSEMENT
                else -> null
            }
        }

    }

}