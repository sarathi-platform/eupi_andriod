package com.sarathi.dataloadingmangement.domain.use_case

import android.net.Uri
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.api.ApiJournalDatabaseRepositoryImpl
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.data.entities.FormEntity
import com.sarathi.dataloadingmangement.download_manager.DownloaderManager
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_FORM_DETAILS
import com.sarathi.dataloadingmangement.network.request.FormDetailRequest
import com.sarathi.dataloadingmangement.repository.FormRepositoryImpl
import javax.inject.Inject

class FormUseCase @Inject constructor(
    private val repository: FormRepositoryImpl,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val downloaderManager: DownloaderManager,
    private val apiJournalDatabaseRepository: ApiJournalDatabaseRepositoryImpl,
) : BaseApiCallNetworkUseCase() {
    private suspend fun getFormDetailFromApi(formDetailRequest: FormDetailRequest): Boolean {
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
            val formEntities = mutableListOf<FormEntity>()
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

    override suspend fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        customData: Map<String, Any>
    ): Boolean {
        try {
            if (!super.invoke(screenName, triggerType, customData)) {
                return false
            }
            //TODO need to add MissionId
            val missionId = customData["MissionId"] as Int
            repository.getActivityConfigUiModel(missionId)?.forEach { config ->
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

    suspend fun getOnlyGeneratedFormSummaryData(
        activityId: Int,
        isFormGenerated: Boolean
    ): List<FormEntity> {
        return repository.getOnlyGeneratedFormSummaryData(
            activityId = activityId,
            isFormGenerated = isFormGenerated
        )
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

    fun getFormEFileName(pdfName: String): String {
        return repository.getFormEFileName(pdfName)
    }

    override fun getApiEndpoint(): String {
        return SUBPATH_GET_FORM_DETAILS
    }

}
