package com.sarathi.dataloadingmangement.domain.use_case.smallGroup

import com.nudge.core.SHG_VERIFICATION
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.sarathi.dataloadingmangement.repository.EventWriterRepositoryImpl
import com.sarathi.dataloadingmangement.repository.smallGroup.VerificationEventWriterRepository
import javax.inject.Inject

class VerificationEventWriterUseCase @Inject constructor(
    private val verificationEventWriterRepository: VerificationEventWriterRepository,
    private val eventWriterRepositoryImpl: EventWriterRepositoryImpl

) {
    suspend operator fun invoke(subjectId: Int, isFromRegenerate: Boolean) {

        val shgVerificationEventPayloadModel =
            verificationEventWriterRepository.getShgVerificationPayloadModel(subjectId)

        val event = eventWriterRepositoryImpl.createAndSaveEvent(
            eventItem = shgVerificationEventPayloadModel,
            eventName = EventName.SHG_VERIFICATION_EVENT,
            surveyName = SHG_VERIFICATION,
            isFromRegenerate = isFromRegenerate
        )

        event?.let {
            eventWriterRepositoryImpl.saveEventToMultipleSources(
                event,
                listOf(),
                EventType.STATEFUL
            )
        }
    }

}