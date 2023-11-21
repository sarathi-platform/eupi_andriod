package com.patsurvey.nudge.activities.ui.bpc.bpc_didi_list_screens

import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import com.patsurvey.nudge.utils.PatSurveyStatus
import javax.inject.Inject

class BPCDidiListRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val tolaDao: TolaDao,
    val stepsListDao: StepsListDao,
    val questionListDao: QuestionListDao,
    val answerDao: AnswerDao
):BaseRepository() {

    fun getAllTolasForVillage(): List<TolaEntity>{
       return tolaDao.getAllTolasForVillage(prefRepo.getSelectedVillage().id)
    }

    fun getAllStepsForVillage(): List<StepListEntity>{
       return stepsListDao.getAllStepsForVillage(prefRepo.getSelectedVillage().id)
    }

    fun getAllDidisForVillage(): List<DidiEntity>{
        return didiDao.getAllDidisForVillage(prefRepo.getSelectedVillage().id)
    }

    fun updateQuesSectionStatus(didiId: Int, patSurveyProgress: Int){
        didiDao.updateQuesSectionStatus(
            didiId,
            patSurveyProgress
        )
    }

    fun updateNeedToPostPAT(needsToPostPAT: Boolean,didiId: Int){
        didiDao.updateNeedToPostPAT(needsToPostPAT, didiId, prefRepo.getSelectedVillage().id)
    }
    fun getAllPendingPATDidisCount(): Int{
       return didiDao.getAllPendingPATDidisCount(prefRepo.getSelectedVillage().id)
    }

    fun isStepComplete(stepId: Int,villageId: Int): Int{
       return stepsListDao.isStepComplete(stepId, villageId)
    }
}