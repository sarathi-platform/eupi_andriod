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
    override suspend fun saveSurveyAnswer(
        question: QuestionUiModel,
        subjectId: Int,
        taskId: Int,
        referenceId: String
    ) {

        surveyAnswersDao.insertOrModifySurveyAnswer(
            SurveyAnswerEntity.getSurveyAnswerEntity(
                question = question,
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                subjectId = subjectId,
                referenceId = referenceId,
                taskId = taskId
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
            if (it.isSelected == true) {
                if (it.selectedValue?.isNotBlank() == true) {
                    result.add(it.selectedValue ?: BLANK_STRING)
                } else {
                    result.add(it.description ?: BLANK_STRING)
                }
            }
        }
        return result.joinToString(",")
    }

    override fun getUserIdentifier(): String {
        return coreSharedPrefs.getUniqueUserIdentifier()
    }

    override suspend fun getAllSaveAnswer(
        surveyId: Int,
        taskId: Int,
        sectionId: Int
    ): List<SurveyAnswerEntity> {
        return surveyAnswersDao.getSurveyAnswersForSummary(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            sectionId = sectionId,
            surveyId = surveyId,
            taskId = taskId
        )
    }

    override suspend fun deleteSurveyAnswer(
        sectionId: Int,
        surveyId: Int,
        referenceId: String,
        taskId: Int
    ): Int {
        return surveyAnswersDao.deleteSurveyAnswer(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            sectionId = sectionId,
            surveyId = surveyId,
            taskId = taskId,
            referenceId = referenceId
        )
    }

}