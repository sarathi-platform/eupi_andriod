package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.LIVELIHOOD_EVENT_TABLE_NAME
import com.sarathi.dataloadingmangement.model.response.LivelihoodEvent


@Entity(tableName = LIVELIHOOD_EVENT_TABLE_NAME)
data class LivelihoodEventEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("primaryKey")
    @Expose
    @ColumnInfo(name = "primaryKey")
    var primaryKey: Int = 0,
    var id: Int,
    var userId: String,
    var name: String,
    var status: Int,
    var type: String?,

    ) {
    companion object {
        fun getLivelihoodEventEntity(
            userId: String,
            livelihoodEvent: LivelihoodEvent,
        ): LivelihoodEventEntity {

            return LivelihoodEventEntity(
                primaryKey = 0,
                id = livelihoodEvent.id ?: 0,
                userId = userId,
                name = livelihoodEvent.name ?: BLANK_STRING,
                status = livelihoodEvent.status ?: 0,
                type = livelihoodEvent.type
            )
        }

    }
}