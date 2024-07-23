package com.sarathi.dataloadingmangement.domain.use_case

import android.net.Uri
import com.nudge.core.compressImage
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.getFileNameFromURL
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.utils.FileUtils
import com.nudge.core.value
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.repository.EventWriterRepositoryImpl
import com.sarathi.dataloadingmangement.repository.SectionStatusEventWriterRepository
import javax.inject.Inject

class SectionStatusEventWriterUserCase @Inject constructor(
    private val sectionStatusEventWriterRepository: SectionStatusEventWriterRepository,
    private val eventWriterRepositoryImpl: EventWriterRepositoryImpl
) {

    suspend operator fun invoke(surveyId: Int, sectionId: Int, taskId: Int, status: String) {

        val event = sectionStatusEventWriterRepository.writeSectionStatusEvent(
            surveyId,
            sectionId,
            taskId,
            status
        )
        val survey = sectionStatusEventWriterRepository.getSurveyForId(surveyId)

        writeEventInFile(
            eventItem = event,
            eventName = EventName.UPDATE_SECTION_PROGRESS_FOR_DIDI_EVENT,
            surveyName = survey?.surveyName.value(),
            uriList = emptyList()
        )

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
        )?.let {

            eventWriterRepositoryImpl.saveEventToMultipleSources(
                it,
                listOf(),
                EventType.STATEFUL
            )

            uriList?.forEach { uri ->
                compressImage(
                    imageUri = FileUtils.findImageFile(
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

}