package com.sarathi.dataloadingmangement.domain.use_case

import android.net.Uri
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.repository.EventWriterRepositoryImpl
import com.sarathi.dataloadingmangement.repository.ISurveyAnswerEventRepository
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
        uriList: List<Uri>?
    ) {

        val saveAnswerMoneyJournalEventDto = repository.writeMoneyJournalSaveAnswerEvent(
            questionUiModels,
            subjectId,
            subjectType,
            referenceId,
            taskLocalId
        )
        writeEventInFile(
            saveAnswerMoneyJournalEventDto,
            EventName.MONEY_JOURNAL_EVENT,
            questionUiModels.firstOrNull()?.surveyName ?: BLANK_STRING,
            uriList
        )
        questionUiModels.forEach { questionUiModel ->
            val saveAnswerEventDto = repository.writeSaveAnswerEvent(
                questionUiModel,
                subjectId,
                subjectType,
                referenceId,
                taskLocalId
            )
            writeEventInFile(
                saveAnswerEventDto,
                EventName.GRANT_SAVE_RESPONSE_EVENT,
                questionUiModel.surveyName,
                uriList
            )
        }
    }

    private suspend fun <T> writeEventInFile(
        eventItem: T,
        eventName: EventName,
        surveyName: String,
        uriList: List<Uri>?
    ) {
        eventWriterRepositoryImpl.createAndSaveEvent(
            eventItem,
            eventName,
            EventType.STATEFUL,
            surveyName
        )
            ?.let {

                eventWriterRepositoryImpl.saveEventToMultipleSources(
                    it,
                    listOf(),
                    EventType.STATEFUL
                )


                uriList?.forEach { uri ->
                    eventWriterRepositoryImpl.saveImageEventToMultipleSources(
                        it,
                        uri = uri
                    )
                }


            }
    }

}
