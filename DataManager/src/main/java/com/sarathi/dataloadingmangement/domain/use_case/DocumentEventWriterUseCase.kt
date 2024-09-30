package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.PARENT_EVENT_NAME
import com.nudge.core.PARENT_TOPIC_NAME
import com.nudge.core.compressImage
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.nudge.core.getFileNameFromURL
import com.nudge.core.json
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.model.getMetaDataDtoFromString
import com.nudge.core.utils.FileUtils.findImageFile
import com.nudge.core.utils.FileUtils.getImageUri
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.repository.DocumentEventRepositoryImpl
import com.sarathi.dataloadingmangement.repository.EventWriterRepositoryImpl
import java.util.UUID
import javax.inject.Inject

class DocumentEventWriterUseCase @Inject constructor(
    private val repository: DocumentEventRepositoryImpl,
    private val eventWriterRepositoryImpl: EventWriterRepositoryImpl
) {

    suspend fun writeSaveDocumentEvent(
        generatedDate: String,
        documentType: String,
        documentName: String,
        activityId: Int
    ) {

        val saveAnswerEventDto = repository.getSaveDocumentEventDto(
            generatedDate = generatedDate,
            documentType = documentType,
            documentName = getFileNameFromURL(documentName),
            activityId = activityId
        )
        eventWriterRepositoryImpl.createAndSaveEvent(
            saveAnswerEventDto,
            EventName.UPLOAD_DOCUMENT_EVENT,
            EventType.STATEFUL,
            surveyName = ""
        )?.let { event ->

            eventWriterRepositoryImpl.saveEventToMultipleSources(
                event, listOf(), EventType.STATEFUL
            )

            CoreAppDetails.getApplicationDetails()?.activity?.applicationContext?.let { it1 ->
                getImageUri(
                    context = it1,
                    fileName = getFileNameFromURL(documentName)
                )?.let { uri ->
                    compressImage(
                        imageUri = findImageFile(
                            CoreAppDetails.getContext()?.applicationContext!!,
                            getFileNameFromURL(uri.path ?: BLANK_STRING)
                        ).absolutePath,
                        activity = CoreAppDetails.getContext()!!,
                        name = getFileNameFromURL(uri.path ?: BLANK_STRING)
                    )
                    val imageEvent = event.also { eventDetail ->
                        eventDetail.id = UUID.randomUUID().toString()
                        val metaData = eventDetail.metadata?.getMetaDataDtoFromString()
                        metaData?.let { metadataDto ->
                            metadataDto.data = mapOf(
                                PARENT_EVENT_NAME to eventDetail.name,
                                PARENT_TOPIC_NAME to eventDetail.type
                            )
                            eventDetail.metadata = metadataDto.json()
                        }
                        eventDetail.name = EventName.UPLOAD_IMAGE_EVENT.topicName
                        eventDetail.type = EventName.UPLOAD_IMAGE_EVENT.topicName


                    }
                    eventWriterRepositoryImpl.saveImageEventToMultipleSources(
                        imageEvent,
                        uri = uri
                    )
                }
            }


        }

    }
}
