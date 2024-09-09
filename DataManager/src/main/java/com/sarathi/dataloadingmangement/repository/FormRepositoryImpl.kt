package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDateInMMDDYYFormat
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.data.dao.FormDao
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.data.entities.FormEntity
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.network.response.FormDetailResponseModel
import javax.inject.Inject

class FormRepositoryImpl @Inject constructor(
    private val formDao: FormDao,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val dataLoadingApiService: DataLoadingApiService,
    private val activityConfigDao: ActivityConfigDao
) : IFormRepository {
    override suspend fun getFromDetailFromNetwork(
        activityId: Int,
        surveyId: Int,
        fromType: String
    ): ApiResponseModel<List<FormDetailResponseModel>> {
        return dataLoadingApiService.getFormDetail(
            activityId = activityId,
            surveyId = surveyId,
            formType = fromType
        )
    }

    override suspend fun saveFromToDB(
        subjectId: Int,
        taskId: Int,
        surveyId: Int,
        missionId: Int,
        activityId: Int,
        referenceId: String,
        subjectType: String
    ): FormEntity {
        val formEntity = FormEntity.getFormEntity(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            taskId = taskId,
            surveyId = surveyId,
            subjectId = subjectId,
            subjectType = subjectType,
            missionId = missionId,
            activityId = activityId,
            referenceId = referenceId
        )
        formDao.insertFormData(formEntity)
        return formEntity

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

    override suspend fun getNonGeneratedFormSummaryData(activityId: Int): List<FormEntity> {
        return formDao.getFormSummaryData(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            isFormGenerated = false,
            activityId = activityId
        )
    }

    override suspend fun getOnlyGeneratedFormSummaryData(
        activityId: Int,
        isFormGenerated: Boolean
    ): List<FormEntity> {
        return formDao.getFormSummaryData(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            isFormGenerated = isFormGenerated,
            activityId = activityId
        )
    }

    override suspend fun getAllFormSummaryData(activityId: Int): List<FormEntity> {
        return formDao.getAllFormSummaryData(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            activityId = activityId
        )
    }


    override suspend fun updateFormData(
        isFormGenerated: Boolean,
        localReferenceId: String,
        generatedDate: String
    ) {
        return formDao.updateFormData(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            isFormGenerated = isFormGenerated,
            localReferenceId = localReferenceId,
            generatedDate = generatedDate
        )
    }

    override fun getFormEFileName(pdfName: String): String {
        return "${coreSharedPrefs.getMobileNo()}_${pdfName}_${
            System.currentTimeMillis().toDateInMMDDYYFormat()
        }"
    }

    override suspend fun saveAllFormDetails(formDetails: List<FormEntity>) {
        formDao.insertAllFormDetail(formDetails)
    }

    override suspend fun getActivityConfigUiModel(): List<ActivityConfigEntity>? {
        return activityConfigDao.getActivityConfigUiModel(userId = coreSharedPrefs.getUniqueUserIdentifier())
    }
}