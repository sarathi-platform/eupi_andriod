package com.sarathi.dataloadingmangement.domain.use_case

import android.net.Uri
import com.nudge.core.constants.DataLoadingTriggerType
import com.nudge.core.data.repository.BaseApiCallNetworkUseCase
import com.nudge.core.data.repository.IApiCallJournalRepository
import com.nudge.core.enums.ActivityTypeEnum
import com.nudge.core.enums.ApiStatus
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.SUCCESS_CODE
import com.sarathi.dataloadingmangement.data.entities.FormEntity
import com.sarathi.dataloadingmangement.download_manager.DownloaderManager
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.network.SUBPATH_GET_FORM_DETAILS
import com.sarathi.dataloadingmangement.network.request.FormDetailRequest
import com.sarathi.dataloadingmangement.repository.FormRepositoryImpl
import java.util.Locale
import javax.inject.Inject

class FormUseCase @Inject constructor(
    private val repository: FormRepositoryImpl,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val downloaderManager: DownloaderManager,
    apiCallJournalRepository: IApiCallJournalRepository,
    private val fetchMissionActivityDetailDataUseCase: FetchMissionActivityDetailDataUseCase
) : BaseApiCallNetworkUseCase(apiCallJournalRepository) {
    private suspend fun getFormDetailFromApi(
        formDetailRequest: FormDetailRequest, screenName: String,
        triggerType: DataLoadingTriggerType,
        moduleName: String,
        customData: Map<String, Any>
    ): Boolean {
        try {
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
                }
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.SUCCESS.name,
                    customData = customData,
                    errorMsg = BLANK_STRING
                )
                return true
            } else {
                updateApiCallStatus(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    status = ApiStatus.FAILED.name,
                    customData = customData,
                    errorMsg = apiResponse.message
                )
                return false
            }
        } catch (apiException: ApiException) {
            updateApiCallStatus(
                screenName = screenName,
                moduleName = moduleName,
                triggerType = triggerType,
                status = ApiStatus.FAILED.name,
                customData = customData,
                errorMsg = apiException.stackTraceToString()
            )
            throw apiException
        } catch (ex: Exception) {
            updateApiCallStatus(
                screenName = screenName,
                moduleName = moduleName,
                triggerType = triggerType,
                status = ApiStatus.FAILED.name,
                customData = customData,
                errorMsg = ex.stackTraceToString()
            )
            throw ex
        }
    }

    override suspend fun invoke(
        screenName: String,
        triggerType: DataLoadingTriggerType,
        moduleName: String,
        customData: Map<String, Any>
    ): Boolean {
        try {
            val missionId: Int = if (customData["MissionId"] != null) {
                customData["MissionId"] as? Int ?: -1
            } else {
                -1
            }
            val activityTypes =
                fetchMissionActivityDetailDataUseCase.getActivityTypesForMission(missionId)
            if (screenName == "ActivityScreen" && !(activityTypes.contains(
                    ActivityTypeEnum.GRANT.name.lowercase(Locale.ENGLISH)
                ) || activityTypes.contains(
                    ActivityTypeEnum.LIVELIHOOD_PoP.name.lowercase(Locale.ENGLISH)
                ))
            ) {
                return false
            }
            if (!super.invoke(
                    screenName = screenName,
                    triggerType = triggerType,
                    moduleName = moduleName,
                    customData = customData,
                )
            ) {
                return false
            }
            //val missionId = customData["MissionId"] as Int
            repository.getActivityConfigUiModel(missionId)?.forEach { config ->
                getFormDetailFromApi(
                    screenName = screenName,
                    moduleName = moduleName,
                    triggerType = triggerType,
                    customData = customData,
                    formDetailRequest = FormDetailRequest(
                        activityId = config.activityId,
                        surveyId = config.surveyId,
                        formType = "Form_E"
                    )
                )
            }
        } catch (ex: Exception) {
            throw ex
        }
        return false
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
