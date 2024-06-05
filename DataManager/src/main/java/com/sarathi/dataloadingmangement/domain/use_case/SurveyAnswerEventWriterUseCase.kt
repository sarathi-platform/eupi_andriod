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
        questionUiModel: List<QuestionUiModel>,
        subjectId: Int,
        subjectType: String,
        referenceId: Int,
        taskLocalId: String,
        uriList: List<Uri>?
    ) {

        val saveAnswerEventDto = repository.writeSaveAnswerEvent(
            questionUiModel,
            subjectId,
            subjectType,
            referenceId,
            taskLocalId
        )
        eventWriterRepositoryImpl.createAndSaveEvent(
            saveAnswerEventDto,
            EventName.SAVE_RESPONSE_EVENT,
            EventType.STATEFUL,
            questionUiModel.firstOrNull()?.surveyName ?: BLANK_STRING
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