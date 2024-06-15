package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.FormEntity
import com.sarathi.dataloadingmangement.data.entities.UiConfigEntity
import com.sarathi.dataloadingmangement.model.uiModel.SubjectAttributes

interface IFormRepository {
    suspend fun saveFromToDB(
        subjectId: Int,
        taskId: Int,
        surveyId: Int,
        referenceId: String,
        subjectType: String
    )

    suspend fun deleteForm(
        referenceId: String,
        taskId: Int
    ): Int

    suspend fun getFormData(): List<FormEntity>
    suspend fun getFormUiConfig(
        missionId: Int,
        activityId: Int
    ): List<UiConfigEntity>

    suspend fun getTaskAttributes(taskId: Int): List<SubjectAttributes>
    fun getSurveyAnswerForTag(taskId: Int, subjectId: Int, tagId: String): String


}