package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.FormDao
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.SurveyAnswersDao
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.data.entities.FormEntity
import com.sarathi.dataloadingmangement.data.entities.UiConfigEntity
import com.sarathi.dataloadingmangement.model.uiModel.SubjectAttributes
import javax.inject.Inject

class FormRepositoryImpl @Inject constructor(
    private val formDao: FormDao,
    private val uiConfigDao: UiConfigDao,
    private val subjectAttributeDao: SubjectAttributeDao,
    private val surveyAnswersDao: SurveyAnswersDao,
    private val coreSharedPrefs: CoreSharedPrefs
) : IFormRepository {
    override suspend fun saveFromToDB(
        subjectId: Int,
        taskId: Int,
        surveyId: Int,
        referenceId: String,
        subjectType: String
    ) {
        formDao.insertFormData(
            FormEntity.getFormEntity(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                taskId = taskId,
                surveyId = surveyId,
                subjectId = subjectId,
                subjectType = subjectType,
                referenceId = referenceId
            )
        )
    }

    override suspend fun deleteForm(
        referenceId: String,
        taskId: Int
    ): Int {
        return formDao.deleteForm(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            referenceId = referenceId,
            taskId = taskId
        )
    }

    override suspend fun getFormData(): List<FormEntity> {
        return formDao.getFormSummaryData(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            isFormGenerated = false
        )
    }


    override suspend fun getFormUiConfig(
        missionId: Int,
        activityId: Int
    ): List<UiConfigEntity> {
        return uiConfigDao.getActivityUiConfig(
            missionId,
            activityId,
            coreSharedPrefs.getUniqueUserIdentifier()
        )
    }

    override suspend fun getTaskAttributes(taskId: Int): List<SubjectAttributes> {
        return subjectAttributeDao.getSubjectAttributes(taskId)
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
            result.add("${it.paraphrase} ${it.selectedValue}")
        }
        return result.joinToString(",")
    }


}