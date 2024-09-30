package com.sarathi.dataloadingmangement.domain.use_case.smallGroup

import com.nudge.core.SMALL_GROUP_ATTENDANCE_MISSION
import com.nudge.core.enums.EventName
import com.nudge.core.enums.EventType
import com.sarathi.dataloadingmangement.repository.EventWriterRepositoryImpl
import com.sarathi.dataloadingmangement.repository.smallGroup.AttendanceEventWriterRepository
import javax.inject.Inject

class AttendanceEventWriterUseCase @Inject constructor(
    private val attendanceEventWriterRepository: AttendanceEventWriterRepository,
    private val eventWriterRepositoryImpl: EventWriterRepositoryImpl
) {

    suspend operator fun invoke(isFromRegenerate: Boolean) {

        val activeAttendanceHistory =
            attendanceEventWriterRepository.getAllActiveAttendanceForUser()

        activeAttendanceHistory.forEach { saveAttendanceEventDto ->
            val event = eventWriterRepositoryImpl.createAndSaveEvent(
                eventItem = saveAttendanceEventDto,
                eventName = EventName.SAVE_SUBJECT_ATTENDANCE_EVENT,
                surveyName = SMALL_GROUP_ATTENDANCE_MISSION,
                isFromRegenerate = isFromRegenerate
            )
            event?.let {
                eventWriterRepositoryImpl.saveEventToMultipleSources(
                    it,
                    listOf(),
                    EventType.STATEFUL
                )
            }

        }

        val deletedAttendanceHistory =
            attendanceEventWriterRepository.getAllDeletedAttendanceForUser()
        deletedAttendanceHistory.forEach { saveAttendanceEventDto ->
            val event = eventWriterRepositoryImpl.createAndSaveEvent(
                eventItem = saveAttendanceEventDto,
                eventName = EventName.DELETE_SUBJECT_ATTENDANCE_EVENT,
                surveyName = SMALL_GROUP_ATTENDANCE_MISSION,
                isFromRegenerate = isFromRegenerate
            )
            event?.let {
                eventWriterRepositoryImpl.saveEventToMultipleSources(
                    it,
                    listOf(),
                    EventType.STATEFUL
                )
            }
        }

    }

}
