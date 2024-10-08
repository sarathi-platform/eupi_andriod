package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.LIVELIHOOD_EVENT_TABLE_NAME
import com.sarathi.dataloadingmangement.model.response.LivelihoodEvent

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
                livelihoodId = livelihoodId
            )
        }

    }
}

