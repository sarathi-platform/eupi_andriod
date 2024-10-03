package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.data.entities.FormEntity
import com.sarathi.dataloadingmangement.network.response.FormDetailResponseModel

interface IFormRepository {
    suspend fun getFromDetailFromNetwork(
        activityId: Int,
        surveyId: Int,
        fromType: String
    ): ApiResponseModel<List<FormDetailResponseModel>>

    suspend fun saveFromToDB(
        subjectId: Int,
        taskId: Int,
        surveyId: Int,
        missionId: Int,
        activityId: Int,
        referenceId: String,
        subjectType: String
    ): FormEntity

    suspend fun deleteForm(
        referenceId: String,
        taskId: Int
    ): Int

    suspend fun getNonGeneratedFormSummaryData(activityId: Int): List<FormEntity>
    suspend fun getOnlyGeneratedFormSummaryData(
        activityId: Int,
        isFormGenerated: Boolean
    ): List<FormEntity>

    suspend fun getAllFormSummaryData(activityId: Int): List<FormEntity>


    suspend fun updateFormData(
        isFormGenerated: Boolean,
        localReferenceId: String,
        generatedDate: String
    )

    fun getFormEFileName(pdfName: String): String

    suspend fun saveAllFormDetails(formDetails: List<FormEntity>)
    suspend fun getActivityConfigUiModel(missionId: Int): List<ActivityConfigEntity>?

}