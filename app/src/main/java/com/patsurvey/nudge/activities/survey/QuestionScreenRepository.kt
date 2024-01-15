package com.patsurvey.nudge.activities.survey

import com.patsurvey.nudge.base.BaseRepository
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.NumericAnswerEntity
import com.patsurvey.nudge.database.QuestionEntity
import com.patsurvey.nudge.database.SectionAnswerEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.NumericAnswerDao
import com.patsurvey.nudge.database.dao.QuestionListDao
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import javax.inject.Inject

class QuestionScreenRepository@Inject constructor(
    val prefRepo: PrefRepo,
    val villageListDao: VillageListDao,
    val questionListDao: QuestionListDao,
    val answerDao: AnswerDao,
    val apiService: ApiService,
    val numericAnswerDao: NumericAnswerDao,
    val stepsListDao: StepsListDao
):BaseRepository() {
    fun getQuestionForSection(sectionType:String):List<QuestionEntity>?{
        return questionListDao.getQuestionForType(
            sectionType,
            prefRepo.getAppLanguageId() ?: 2
        )
    }
    fun getSectionAnswersForDidi(actionType: String,didiId:Int):List<SectionAnswerEntity>?{
        return answerDao.getAnswerForDidi(actionType, didiId = didiId)
    }

    fun getAllAnswerForDidi(didiId: Int): List<NumericAnswerEntity>?{
      return numericAnswerDao.getAllAnswersForDidi(didiId)
    }
    fun updateDidiQuestionSection(didiId: Int,status: Int,sectionType: String){
        if(prefRepo.isUserBPC()){
            didiDao.updateQuesSectionStatus(didiId, status)
            if (sectionType.equals(TYPE_EXCLUSION, true)) {
                didiDao.updatePatSection1Status(didiId, status)
                didiDao.updatePATEditStatus(didiId,true)
            } else didiDao.updatePatSection2Status(didiId, status)

        } else {
            didiDao.updateQuesSectionStatus(didiId, status)
            if (sectionType.equals(TYPE_EXCLUSION, true)) {
                didiDao.updatePatSection1Status(didiId, status)
            } else didiDao.updatePatSection2Status(didiId, status)
        }
    }
    fun updateNeedToPostPAT(didiId: Int) {
        didiDao.updateNeedToPostPAT(true, didiId, villageId = prefRepo.getSelectedVillage().id)
        if (prefRepo.isUserBPC()) {
            didiDao.updateNeedsToPostBPCProcessStatus(true, didiId)
        }
    }

    fun isAlreadyAnswered(didiId: Int, questionId: Int, sectionType: String): Int {
        return answerDao.isAlreadyAnswered(
            didiId = didiId,
            questionId = questionId,
            actionType = sectionType
        )
    }

    fun updateDidiAnswer(
        didiId: Int,
        optionId: Int,
        questionId: Int,
        actionType: String,
        optionValue: Int,
        weight: Int,
        answerValue: String,
        type: String,
        totalAssetAmount: Double,
        summary: String,
        assetAmount: String,
        questionFlag: String
    ) {
        answerDao.updateAnswer(
            didiId = didiId, questionId = questionId,
            actionType = actionType,
            answerValue = answerValue,
            optionValue = optionValue,
            optionId = optionId,
            weight = weight,
            type = type,
            totalAssetAmount = totalAssetAmount,
            summary = summary,
            assetAmount = assetAmount,
            questionFlag = questionFlag
        )
    }

    fun updateAnswerNeedToPost(didiId: Int, questionId: Int, needsToPost: Boolean) {
        answerDao.updateNeedToPost(didiId, questionId, needsToPost)
    }

    fun updateAllAnswerNeedToPost(didiId: Int,needsToPost: Boolean){
        answerDao.updateAllAnswersNeedToPost(didiId, needsToPost)
    }

    fun insertAnswer(sectionAnswerEntity: SectionAnswerEntity){
        answerDao.insertAnswer(
            sectionAnswerEntity
        )
    }
    fun getAnswerOptionDetails(optionId:Int,questionId:Int,didiId:Int):NumericAnswerEntity{
       return numericAnswerDao.getOptionDetails(
            optionId,
            questionId, didiId
        )
    }

    fun updateNumericAnswer(
        didiId: Int,
        optionId: Int,
        questionId: Int,
        count: Int,
        optionValue: Int
    ) {
        numericAnswerDao.updateAnswer(
            didiId,
            optionId,
            questionId,
            count,
            optionValue
        )
    }

    fun insertNumericAnswer(numericAnswer:NumericAnswerEntity){
        numericAnswerDao.insertNumericOption(numericAnswer)
    }

    fun getTotalAssetAmountFromDB(questionId: Int,didiId: Int):List<Int>{
       return numericAnswerDao.getTotalAssetAmount(questionId,didiId)
    }

    fun isQuestionAnswered(didiId: Int,questionId: Int):Int{
       return answerDao.isQuestionAnswered(didiId, questionId = questionId)
    }

    fun fetchOptionIdFromDB(didiId: Int, questionId: Int, actionType: String): Int {
        return answerDao.fetchOptionID(
            didiId = didiId,
            questionId = questionId,
            actionType = actionType
        )
    }

    fun fetchTotalAmount(questionId:Int,didiId:Int):Int{
        return numericAnswerDao.fetchTotalAmount(questionId,didiId)
    }
}

