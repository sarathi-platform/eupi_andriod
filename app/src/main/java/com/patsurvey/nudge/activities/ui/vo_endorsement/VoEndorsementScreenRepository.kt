package com.patsurvey.nudge.activities.ui.vo_endorsement

import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.TolaDao
import javax.inject.Inject

class VoEndorsementScreenRepository @Inject constructor(
    val prefRepo: PrefRepo,
    val tolaDao: TolaDao,
    val questionListDao: QuestionListDao,
    val answerDao: AnswerDao,
    val numericAnswerDao: NumericAnswerDao,
    val stepsListDao: StepsListDao
):BaseRepository() {

    fun fetchVOEndorseStatusDidi():List<DidiEntity>{
        return didiDao.fetchVOEndorseStatusDidi(prefRepo.getSelectedVillage().id)
    }

    fun checkVoEndorsementStepStatus(stepId:Int,villageId:Int): Int{
        return stepsListDao.isStepComplete(stepId, villageId)
    }

}