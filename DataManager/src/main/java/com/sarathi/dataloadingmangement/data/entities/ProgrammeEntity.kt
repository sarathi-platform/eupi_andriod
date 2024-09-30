package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.PROGRAMME_TABLE_NAME
import com.sarathi.dataloadingmangement.model.mat.response.ProgrameResponse

@Entity(tableName = PROGRAMME_TABLE_NAME)
data class ProgrammeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,
    val actualStartDate: String,
    val programmeId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("startDate")
    val startDate: String,
    val userId: String
) {
    companion object {

        fun getProgrammeEntity(
            programmeResponse: ProgrameResponse,
            uniqueUserIdentifier: String
        ): ProgrammeEntity {
            return ProgrammeEntity(
                id = 0,
                actualStartDate = programmeResponse.actualStartDate ?: BLANK_STRING,
                name = programmeResponse.name,
                programmeId = programmeResponse.id,
                startDate = programmeResponse.startDate ?: BLANK_STRING,
                userId = uniqueUserIdentifier
            )

        }
    }
}