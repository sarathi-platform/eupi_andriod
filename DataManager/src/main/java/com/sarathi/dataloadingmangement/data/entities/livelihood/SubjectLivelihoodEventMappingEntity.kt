package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sarathi.dataloadingmangement.SUBJECT_LIVELIHOOD_EVENT_MAPPING_TABLE_NAME

@Entity(tableName = SUBJECT_LIVELIHOOD_EVENT_MAPPING_TABLE_NAME)
data class SubjectLivelihoodEventMappingEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val transactionId: String,
    val date: Long,
    val livelihoodId: Int,
    val livelihoodEventId: Int,
    val livelihoodEventType: String

)
