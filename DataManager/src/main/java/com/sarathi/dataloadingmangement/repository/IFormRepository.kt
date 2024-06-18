package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.FormEntity

interface IFormRepository {
    suspend fun saveFromToDB(
        subjectId: Int,
        taskId: Int,
        surveyId: Int,
        missionId: Int,
        activityId: Int,
        referenceId: String,
        subjectType: String
    )

    suspend fun deleteForm(
        referenceId: String,
        taskId: Int
    ): Int

    suspend fun getNonGeneratedFormSummaryData(activityId: Int): List<FormEntity>
    suspend fun getAllFormSummaryData(activityId: Int): List<FormEntity>


    suspend fun updateFormData(
        isFormGenerated: Boolean
    )


}