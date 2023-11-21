package com.patsurvey.nudge.activities.survey

import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.updateStepStatus
import javax.inject.Inject

class PatSectionSummaryRepository @Inject constructor(
    val prefRepo: PrefRepo,
    private val questionListDao: QuestionListDao,
    private val answerDao: AnswerDao,
    private val stepsListDao: StepsListDao
):BaseRepository() {

    fun getAllStepsForVillage():List<StepListEntity>{
       return stepsListDao.getAllStepsForVillage(prefRepo.getSelectedVillage().id)
    }

    fun getAllQuestionForLanguage():List<QuestionEntity>{
        return questionListDao.getAllQuestionsForLanguage(prefRepo.getAppLanguageId()?:2)
    }
    fun getQuestionForType(questionType: String):List<QuestionEntity> {
       return questionListDao.getQuestionForType(questionType,prefRepo.getAppLanguageId()?:2)
    }

    fun getAnswerForDidi(actionType: String,didiId:Int): List<SectionAnswerEntity>{
      return answerDao.getAnswerForDidi(actionType, didiId = didiId)
    }

    fun setPatSurveyComplete(didiId: Int,status: Int){
        didiDao.updateQuesSectionStatus(didiId,status)
        didiDao.updateDidiNeedToPostPat(didiId, true)
    }

    fun updatePatSection1Status(didiId: Int, section1: Int){
        didiDao.updatePatSection1Status(didiId,section1)
    }
    fun updatePatSection2Status(didiId: Int, section2: Int){
        didiDao.updatePatSection2Status(didiId,section2)
    }

    fun updateVillageStepStatus(didiId:Int){
        updateStepStatus(
            stepsListDao = stepsListDao,
            didiDao = didiDao,
            didiId = didiId,
            prefRepo = prefRepo,
            printTag = "PatSectionSummaryRepository ONE"
        )
    }

    fun fetchOptionYesCount(didiId: Int):Int{
       return answerDao.fetchOptionYesCount(didiId = didiId, QuestionType.RadioButton.name,TYPE_EXCLUSION)
    }

    fun updateExclusionStatus(didiId: Int, patExclusionStatus: Int, crpComment:String){
        didiDao.updateExclusionStatus(didiId,patExclusionStatus, crpComment)
    }

    fun getAllInclusiveQues(didiId: Int):List<SectionAnswerEntity>{
       return answerDao.getAllInclusiveQues(didiId = didiId)
    }

    fun getTotalWeightWithoutNumQues(didiId: Int):Double{
      return answerDao.getTotalWeightWithoutNumQues(didiId)
    }

    fun getQuestion(questionId:Int):QuestionEntity{
       return questionListDao.getQuestion(questionId)
    }

    fun updateVOEndorsementDidiStatus(didiId:Int,status: Int){
        didiDao.updateVOEndorsementDidiStatus(
            prefRepo.getSelectedVillage().id,
            didiId,
            status
        )
    }

    fun updateDidiScoreInDB(score: Double,comment:String,isDidiAccepted:Boolean,didiId: Int){
        didiDao.updateDidiScore(
            score = score,
            comment = comment,
            didiId = didiId,
            isDidiAccepted = isDidiAccepted
        )
    }

    fun updateModifiedDateServerId(didiId: Int){
        didiDao.updateModifiedDateServerId(System.currentTimeMillis(), didiId)

    }




}