package com.patsurvey.nudge.activities.ui.vo_endorsement

import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import javax.inject.Inject

class VoEndorsementSummaryRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val answerDao: AnswerDao,
    val questionListDao: QuestionListDao,
    val stepsListDao: StepsListDao
):BaseRepository() {
    fun getAllQuestionsForLanguage():List<QuestionEntity>{
        return questionListDao.getAllQuestionsForLanguage(prefRepo.getAppLanguageId()?:2)
    }

    fun fetchVOEndorseStatusDidi(): List<DidiEntity>{
       return didiDao.fetchVOEndorseStatusDidi(prefRepo.getSelectedVillage().id)
    }

    fun getAllStepsForVillage(): List<StepListEntity>{
       return stepsListDao.getAllStepsForVillage(prefRepo.getSelectedVillage().id)
    }

    fun getAnswerForDidi(didiId:Int,actionType:String): List<SectionAnswerEntity>{
        return answerDao.getAnswerForDidi(didiId = didiId, actionType = actionType)
    }

    fun updateVOEndorsementStatus(villageId: Int,didiId:Int,status:Int){
        didiDao.updateVOEndorsementStatus(villageId = villageId, didiId, status)
    }

    fun updateNeedToPostVO(needsToPostVo: Boolean,didiId: Int,villageId: Int){
        didiDao.updateNeedToPostVO(
            didiId = didiId,
            needsToPostVo = needsToPostVo,
            villageId = villageId
        )
    }
}