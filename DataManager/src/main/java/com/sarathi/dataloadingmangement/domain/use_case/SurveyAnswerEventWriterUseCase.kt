package com.sarathi.dataloadingmangement.domain.use_case

import android.net.Uri
import android.text.TextUtils
import com.nudge.core.compressImage
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.getFileNameFromURL
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.utils.FileUtils.findImageFile
import com.nudge.core.utils.FileUtils.getImageUri
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.events.BaseSaveAnswerEventDto
import com.sarathi.dataloadingmangement.model.events.SaveAnswerMoneyJorunalEventDto
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.repository.EventWriterRepositoryImpl
import com.sarathi.dataloadingmangement.repository.ISurveyAnswerEventRepository
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import javax.inject.Inject

class SurveyAnswerEventWriterUseCase @Inject constructor(
    private val repository: ISurveyAnswerEventRepository,
    private val eventWriterRepositoryImpl: EventWriterRepositoryImpl
) {
    suspend operator fun invoke(
        questionUiModels: List<QuestionUiModel>,
        subjectId: Int,
        subjectType: String,
        referenceId: String,
        taskLocalId: String,
        grantId: Int,
        grantType: String,
        taskId: Int,
        isFromRegenerate: Boolean,
        activityId: Int,
        activityReferenceId: Int?,
        activityReferenceType: String?
    ) {
        val uriList = ArrayList<Uri>()
        val saveAnswerMoneyJournalEventDto = saveAnswerMoneyJorunalEventDto(
            questionUiModels,
            subjectId,
            subjectType,
            referenceId,
            taskLocalId,
            grantId,
            grantType,
            taskId
        )
        writeEventInFile(
            saveAnswerMoneyJournalEventDto,
            EventName.MONEY_JOURNAL_EVENT,
            questionUiModels.firstOrNull()?.surveyName ?: BLANK_STRING,
            listOf(),
            isFromRegenerate = isFromRegenerate
        )
        saveFormResponseEvent(saveAnswerMoneyJournalEventDto, questionUiModels, isFromRegenerate)
        questionUiModels.forEach { questionUiModel ->
            saveSurveyAnswerEvent(
                questionUiModel,
                subjectId,
                subjectType,
                referenceId,
                taskLocalId,
                grantId,
                grantType,
                taskId,
                uriList,
                isFromRegenerate = isFromRegenerate,
                activityId = activityId,
                activityReferenceId = activityReferenceId,
                activityReferenceType = activityReferenceType

            )
        }
    }

    suspend fun fetchQuestionAnswerEventList(
        questionUiModel: QuestionUiModel,
        subjectId: Int,
        subjectType: String,
        referenceId: String,
        taskLocalId: String,
        grantId: Int,
        grantType: String,
        taskId: Int,
        activityId: Int,
        activityReferenceId: Int?,
        activityReferenceType: String?
    ): BaseSaveAnswerEventDto {
        var surveyAnswerModel: BaseSaveAnswerEventDto? = null
        surveyAnswerModel = getSurveyAnswerEvent(
            questionUiModel,
            subjectId,
            subjectType,
            referenceId,
            taskLocalId,
            grantId,
            grantType,
            taskId,
            activityId = activityId,
            activityReferenceId = activityReferenceId,
            activityReferenceType = activityReferenceType

        )

        return surveyAnswerModel
    }

    private suspend fun saveAnswerMoneyJorunalEventDto(
        questionUiModels: List<QuestionUiModel>,
        subjectId: Int,
        subjectType: String,
        referenceId: String,
        taskLocalId: String,
        grantId: Int,
        grantType: String,
        taskId: Int
    ): SaveAnswerMoneyJorunalEventDto {
        val saveAnswerMoneyJournalEventDto = repository.writeMoneyJournalSaveAnswerEvent(
            questionUiModels,
            subjectId,
            subjectType,
            referenceId,
            taskLocalId,
            grantId,
            grantType,
            taskId,
            repository.getTagIdForSection(
                sectionId = questionUiModels.firstOrNull()?.sectionId ?: -1
            )
        )
        return saveAnswerMoneyJournalEventDto
    }

    suspend fun writeFormResponseEvent(
        questionUiModels: List<QuestionUiModel>,
        subjectId: Int,
        subjectType: String,
        referenceId: String,
        taskLocalId: String,
        grantId: Int,
        grantType: String,
        taskId: Int,
        isFromRegenerate: Boolean,
    ) {
        val saveAnswerMoneyJournalEventDto = saveAnswerMoneyJorunalEventDto(
            questionUiModels,
            subjectId,
            subjectType,
            referenceId,
            taskLocalId,
            grantId,
            grantType,
            taskId
        )
        saveFormResponseEvent(
            saveAnswerMoneyJournalEventDto,
            questionUiModels = questionUiModels,
            isFromRegenerate = isFromRegenerate
        )
    }

    private suspend fun SurveyAnswerEventWriterUseCase.saveFormResponseEvent(
        saveAnswerMoneyJournalEventDto: SaveAnswerMoneyJorunalEventDto,
        questionUiModels: List<QuestionUiModel>,
        isFromRegenerate: Boolean
    ) {
        writeEventInFile(
            saveAnswerMoneyJournalEventDto,
            EventName.FORM_RESPONSE_EVENT,
            questionUiModels.firstOrNull()?.surveyName ?: BLANK_STRING,
            listOf(),
            isFromRegenerate = isFromRegenerate

        )
    }

    suspend fun getSurveyAnswerEvent(
        questionUiModel: QuestionUiModel,
        subjectId: Int,
        subjectType: String,
        referenceId: String,
        taskLocalId: String,
        grantId: Int,
        grantType: String,
        taskId: Int,
        activityId: Int,
        activityReferenceId: Int?,
        activityReferenceType: String?
    ): BaseSaveAnswerEventDto {
        return repository.writeSaveAnswerEvent(
            questionUiModel = questionUiModel,
            subjectId = subjectId,
            subjectType = subjectType,
            refrenceId = referenceId,
            taskLocalId = taskLocalId,
            grantId = grantId,
            grantType = grantType,
            taskId = taskId,
            activityId = activityId,
            activityReferenceId = activityReferenceId,
            activityReferenceType = activityReferenceType
        )
    }
    suspend fun saveSurveyAnswerEvent(
        questionUiModel: QuestionUiModel,
        subjectId: Int,
        subjectType: String,
        referenceId: String,
        taskLocalId: String,
        grantId: Int,
        grantType: String,
        taskId: Int,
        uriList: ArrayList<Uri>,
        isFromRegenerate: Boolean,
        activityId: Int,
        activityReferenceId: Int?,
        activityReferenceType: String?
    ) {
        val saveAnswerEventDto = repository.writeSaveAnswerEvent(
            questionUiModel = questionUiModel,
            subjectId = subjectId,
            subjectType = subjectType,
            refrenceId = referenceId,
            taskLocalId = taskLocalId,
            grantId = grantId,
            grantType = grantType,
            taskId = taskId,
            activityId = activityId,
            activityReferenceId = activityReferenceId,
            activityReferenceType = activityReferenceType
        )
        if (questionUiModel.type == QuestionType.MultiImage.name || questionUiModel.type == QuestionType.SingleImage.name) {
            questionUiModel.options?.firstOrNull()?.selectedValue?.split(",")?.forEach {

                if (!TextUtils.isEmpty(it)) {
                    CoreAppDetails.getApplicationDetails()?.activity?.applicationContext?.let { it1 ->
                        getImageUri(
                            context = it1,
                            fileName = getFileNameFromURL(it)
                        )?.let { it1 ->
                            uriList.add(
                                it1
                            )
                        }
                    }
                }
            }
        }

        writeEventInFile(
            saveAnswerEventDto,
            EventName.GRANT_SAVE_RESPONSE_EVENT,
            questionUiModel.surveyName,
            uriList,
            isFromRegenerate = isFromRegenerate


        )
    }

    private suspend fun <T> writeEventInFile(
        eventItem: T,
        eventName: EventName,
        surveyName: String,
        uriList: List<Uri>?,
        isFromRegenerate: Boolean
    ) {
        eventWriterRepositoryImpl.createAndSaveEvent(
            eventItem,
            eventName,
            EventType.STATEFUL,
            surveyName,
            isFromRegenerate
        )
            ?.let {

                eventWriterRepositoryImpl.saveEventToMultipleSources(
                    it,
                    listOf(),
                    EventType.STATEFUL
                )


                uriList?.forEach { uri ->
                    compressImage(
                        imageUri = findImageFile(
                            CoreAppDetails.getContext()?.applicationContext!!,
                            getFileNameFromURL(uri.path ?: BLANK_STRING)
                        ).absolutePath,
                        activity = CoreAppDetails.getContext()!!,
                        name = getFileNameFromURL(uri.path ?: BLANK_STRING)
                    )
                    eventWriterRepositoryImpl.saveImageEventToMultipleSources(
                        it,
                        uri = uri
                    )
                }


            }
    }

    suspend fun deleteSavedAnswerEvent(
        surveyID: Int,
        surveyName: String,
        sectionId: Int,
        subjectId: Int,
        subjectType: String,
        referenceId: String,
        taskLocalId: String,
        uriList: List<Uri>?,
        grantId: Int,
        grantType: String,
        taskId: Int,
        isFromRegenerate: Boolean
    ) {
        val saveAnswerMoneyJournalEventDto = repository.writeDeleteSaveAnswerEvent(
            surveyID,
            sectionId,
            subjectId,
            subjectType,
            referenceId,
            taskLocalId,
            grantId,
            grantType,
            taskId
        )
        writeEventInFile(
            saveAnswerMoneyJournalEventDto,
            EventName.GRANT_DELETE_RESPONSE_EVENT,
            surveyName ?: BLANK_STRING,
            uriList,
            isFromRegenerate = isFromRegenerate
        )
    }

}
