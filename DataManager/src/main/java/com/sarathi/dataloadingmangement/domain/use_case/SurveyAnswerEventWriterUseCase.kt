package com.sarathi.dataloadingmangement.domain.use_case

import android.net.Uri
import android.text.TextUtils
import com.nudge.core.compressImage
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.getFileNameFromURL
import com.nudge.core.json
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.utils.FileUtils.findImageFile
import com.nudge.core.utils.FileUtils.getImageUri
import com.sarathi.dataloadingmangement.BLANK_STRING
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
        taskId: Int
    ) {
        val uriList = ArrayList<Uri>()
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
        writeEventInFile(
            EventName.MONEY_JOURNAL_EVENT,
            questionUiModels.firstOrNull()?.surveyName ?: BLANK_STRING,
            listOf(),
            requestPayload = saveAnswerMoneyJournalEventDto.json()
        )
        writeEventInFile(
            EventName.FORM_RESPONSE_EVENT,
            questionUiModels.firstOrNull()?.surveyName ?: BLANK_STRING,
            listOf(),
            requestPayload = saveAnswerMoneyJournalEventDto.json()
        )
        questionUiModels.forEach { questionUiModel ->
            val saveAnswerEventDto = repository.writeSaveAnswerEvent(
                questionUiModel,
                subjectId,
                subjectType,
                referenceId,
                taskLocalId,
                grantId,
                grantType,
                taskId
            )
            if (questionUiModel.type == QuestionType.MultiImage.name) {
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
                EventName.GRANT_SAVE_RESPONSE_EVENT,
                questionUiModel.surveyName,
                uriList,
                requestPayload = saveAnswerEventDto.json()
            )
        }
    }

    private suspend fun writeEventInFile(
        eventName: EventName,
        surveyName: String,
        uriList: List<Uri>?,
        requestPayload: String
    ) {
        eventWriterRepositoryImpl.createAndSaveEvent(
            eventName,
            EventType.STATEFUL,
            surveyName,
            requestPayload = requestPayload
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

    suspend fun deleteDisbursementOrReceiptOfFundEvent(
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
        taskId: Int
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
            EventName.GRANT_DELETE_RESPONSE_EVENT,
            surveyName ?: BLANK_STRING,
            uriList,
            requestPayload = saveAnswerMoneyJournalEventDto.json()
        )
    }

}
