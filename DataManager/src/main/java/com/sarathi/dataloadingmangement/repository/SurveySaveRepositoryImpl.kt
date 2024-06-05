package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.entities.SurveyAnswerEntity
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import javax.inject.Inject

class SurveySaveRepositoryImpl @Inject constructor(
    val surveyAnswersDao: SurveyAnswersDao,
    val coreSharedPrefs: CoreSharedPrefs
) :
    ISurveySaveRepository {


    override suspend fun saveSurveyAnswer(question: QuestionUiModel, subjectId: Int) {

        surveyAnswersDao.insertOrModifySurveyAnswer(
            SurveyAnswerEntity.getSurveyAnswerEntity(
                question = question,
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                subjectId = subjectId,
                referenceId = "",
                taskId = 0
            )
        )
    }

    override fun getSurveyAnswerForTag(taskId: Int, subjectId: Int, tagId: String): String {
        val surveyAnswerEntity = surveyAnswersDao.getSurveyAnswerForTag(
            taskId,
            subjectId,
            tagId.toInt(),
            coreSharedPrefs.getUniqueUserIdentifier()
        )
        val result = ArrayList<String>()
        surveyAnswerEntity?.optionItems?.forEach {
            result.add(it.selectedValue ?: BLANK_STRING)
        }
        return result.joinToString(",")
    }

    override fun getUserIdentifier(): String {
        return coreSharedPrefs.getUniqueUserIdentifier()
    }


}