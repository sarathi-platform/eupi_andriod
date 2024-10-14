package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nudge.core.json
import com.sarathi.dataloadingmangement.SUBJECT_LIVELIHOOD_EVENT_MAPPING_TABLE_NAME
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData

@Entity(tableName = SUBJECT_LIVELIHOOD_EVENT_MAPPING_TABLE_NAME)
data class SubjectLivelihoodEventMappingEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val transactionId: String,
    val subjectId: Int,
    val date: Long,
    val livelihoodId: Int,
    val livelihoodEventId: Int,
    val livelihoodEventType: String,
    val surveyResponse: String,
    val status: Int,
    val createdDate: Long,
    val modifiedDate: Long

) {
    companion object {
        fun getSubjectLivelihoodEventMappingEntity(
            uniqueUserIdentifier: String,
            eventData: LivelihoodEventScreenData,
            createdDate: Long,
            modifiedDate: Long,
            status: Int
        ): SubjectLivelihoodEventMappingEntity {
            return SubjectLivelihoodEventMappingEntity(
                userId = uniqueUserIdentifier,
                id = 0,
                transactionId = eventData.transactionId,
                subjectId = eventData.subjectId,
                date = eventData.date,
                livelihoodId = eventData.livelihoodId,
                livelihoodEventId = eventData.eventId,
                livelihoodEventType = eventData.selectedEvent.name,
                surveyResponse = eventData.json(),
                status = status,
                modifiedDate = modifiedDate,
                createdDate = createdDate,
            )
        }

    }
}
