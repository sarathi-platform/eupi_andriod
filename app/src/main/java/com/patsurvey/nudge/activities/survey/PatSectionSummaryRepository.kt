package com.patsurvey.nudge.activities.survey

import com.nudge.core.enums.EventName
import com.nudge.core.eventswriter.entities.EventV1
import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.StepListEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.model.request.AnswerDetailDTOListItem
import com.patsurvey.nudge.model.request.EditDidiWealthRankingRequest
import com.patsurvey.nudge.model.request.PATSummarySaveRequest
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BPC_USER_TYPE
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import com.patsurvey.nudge.utils.QuestionType
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.USER_BPC
import com.patsurvey.nudge.utils.USER_CRP
import com.patsurvey.nudge.utils.json
import com.patsurvey.nudge.utils.updateStepStatus
import javax.inject.Inject

class PatSectionSummaryRepository @Inject constructor(
    val prefRepo: PrefRepo,
    private val questionListDao: QuestionListDao,
    private val answerDao: AnswerDao,
    private val numericAnswerDao: NumericAnswerDao,
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

    fun updateVOEndorsementStatus(didiId: Int,status:Int){
        didiDao.updateVOEndorsementStatus(prefRepo.getSelectedVillage().id,
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

    private fun getAllAnswersForDidi(didiId: Int): List<SectionAnswerEntity> {
        return answerDao.getAllNeedToPostQuesForDidi(didiId)
    }

    private fun getAllNumericAnswersForDidi(didiId: Int): List<NumericAnswerEntity> {
        return numericAnswerDao.getAllAnswersForDidi(didiId)
    }

    suspend fun writePatSummarySaveEvent(didiEntity: DidiEntity) {
        val sectionAnswerEntityList = getAllAnswersForDidi(didiEntity.id)
        val numericAnswerEntityList = getAllNumericAnswersForDidi(didiEntity.id)
        val answerDetailDTOListItem = AnswerDetailDTOListItem.getAnswerDetailDtoListItem(sectionAnswerEntityList, numericAnswerEntityList)
        val patSummarySaveRequest = PATSummarySaveRequest.getPatSummarySaveRequest(
            didiEntity = didiEntity,
            answerDetailDTOList = answerDetailDTOListItem,
            languageId = (prefRepo.getAppLanguageId() ?: 2),
            surveyId = getSurveyId(sectionAnswerEntityList.first().questionId, questionListDao),
            villageEntity = prefRepo.getSelectedVillage(),
            userType = if((prefRepo.getPref(PREF_KEY_TYPE_NAME, "") ?: "").equals(BPC_USER_TYPE, true)) USER_BPC else USER_CRP
        ).json()

        val event = EventV1(
            eventTopic = EventName.SAVE_PAT_ANSWERS.topicName,
            payload = patSummarySaveRequest,
            mobileNumber = prefRepo.getMobileNumber() ?: BLANK_STRING
        )

        writeEventIntoLogFile(event)

    }

    suspend fun writePatScoreSaveEvent(didiEntity: DidiEntity) {
        val passingMark = questionListDao.getPassingScore()
        val patScoreSaveRequest = EditDidiWealthRankingRequest.getRequestPayloadForPatScoreSave(didiEntity, passingMark, isBpcUserType = prefRepo.isUserBPC()).json()

        val event = EventV1(
            eventTopic = EventName.SAVE_PAT_SCORE.topicName,
            payload = patScoreSaveRequest,
            mobileNumber = prefRepo.getMobileNumber() ?: BLANK_STRING
        )

        writeEventIntoLogFile(event)

    }

}