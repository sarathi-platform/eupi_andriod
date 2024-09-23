package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.LIVELIHOOD_EVENT_TABLE_NAME
import com.sarathi.dataloadingmangement.data.converters.ValidationConverter
import com.sarathi.dataloadingmangement.model.response.LivelihoodEvent
import com.sarathi.dataloadingmangement.model.response.Validation

@Entity(tableName = LIVELIHOOD_EVENT_TABLE_NAME)
data class LivelihoodEventEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var eventId: Int,
    var livelihoodId: Int,
    var userId: String,
    var name: String,
    var status: Int,
    var type: String?,
    @TypeConverters(ValidationConverter::class)
    var validations: Validation?
    

    ) {
    companion object {
        fun getLivelihoodEventEntity(
            userId: String,
            livelihoodEvent: LivelihoodEvent,
            livelihoodId: Int
        ): LivelihoodEventEntity {

            return LivelihoodEventEntity(
                id = 0,
                eventId = livelihoodEvent.id ?: 0,
                userId = userId,
                name = livelihoodEvent.name ?: BLANK_STRING,
                status = livelihoodEvent.status ?: 0,
                type = livelihoodEvent.type,
                livelihoodId = livelihoodId,
                validations = livelihoodEvent.validation
            )
        }

    }
}

