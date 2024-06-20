package com.sarathi.dataloadingmangement.domain.use_case

import android.net.Uri
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.data.entities.FormEntity
import com.sarathi.dataloadingmangement.download_manager.DownloaderManager
import com.sarathi.dataloadingmangement.network.request.FormDetailRequest
import com.sarathi.dataloadingmangement.repository.FormRepositoryImpl
import javax.inject.Inject

class FormUseCase @Inject constructor(
    private val repository: FormRepositoryImpl,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val downloaderManager: DownloaderManager
) {
    private suspend fun getFormDetailFromApi(formDetailRequest: FormDetailRequest): Boolean {
        val formEntities = mutableListOf<FormEntity>()
        val apiResponse = repository.getFromDetailFromNetwork(
            activityId = formDetailRequest.activityId,
            surveyId = formDetailRequest.surveyId,
            fromType = formDetailRequest.formType
        )
        if (apiResponse.status.equals(SUCCESS_CODE, true) || apiResponse.status.equals(
                SUCCESS,
                true
            )
        ) {
            apiResponse.data?.let { formDetail ->
                formDetail.forEach { form ->
                    formEntities.add(
                        FormEntity.getFormEntity(
                            userId = coreSharedPrefs.getUniqueUserIdentifier(),
                            formDetailResponse = form
                        )
                    )
                }
                repository.saveAllFormDetails(formEntities)
                return true
            }
        } else {
            return true
        }
        return false
    }

    suspend fun invoke(): Boolean {
        try {
            repository.getActivityConfigUiModel()?.forEach { config ->
                getFormDetailFromApi(
                    FormDetailRequest(
                        activityId = config.activityId,
                        surveyId = config.surveyId,
                        formType = "Form_E"
                    )
                )
            }
        } catch (ex: Exception) {
            throw ex
        }
        return true
    }


    suspend fun saveFormEData(
        subjectId: Int,
        taskId: Int,
        surveyId: Int,
        missionId: Int,
        activityId: Int,
        referenceId: String,
        subjectType: String
    ): FormEntity {
        return repository.saveFromToDB(
            subjectId = subjectId,
            taskId = taskId,
            surveyId = surveyId,
            missionId = missionId,
            activityId = activityId,
            referenceId = referenceId,
            subjectType = subjectType
        )
    }

    suspend fun deleteFormE(
        referenceId: String,
        taskId: Int
    ): Int {
        return repository.deleteForm(
            taskId = taskId,
            referenceId = referenceId
        )
    }

    suspend fun getFormSummaryData(activityId: Int): List<FormEntity> {
        return repository.getAllFormSummaryData(activityId)
    }

    suspend fun getNonGeneratedFormSummaryData(activityId: Int): List<FormEntity> {
        return repository.getNonGeneratedFormSummaryData(activityId)
    }

    fun getFilePathUri(filePath: String): Uri? {
        return downloaderManager.getFilePathUri(filePath)
    }

    suspend fun updateFormData(
        isFormGenerated: Boolean,
        localReferenceId: String,
        generatedDate: String,
    ) {
        repository.updateFormData(isFormGenerated, localReferenceId, generatedDate)
    }

    fun getFormEFileName(): String {
        return repository.getFormEFileName()
    }

}
