package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sarathi.dataloadingmangement.CONDITIONS_TABLE_NAME

@Entity(CONDITIONS_TABLE_NAME)
data class ConditionsEntity(

    @PrimaryKey(true)
    @ColumnInfo("id")
    val id: Int = 0,
    val userId: String?,
    val sourceTargetQuestionRefId: Long,
    val conditions: String
) {

    companion object {

        fun getConditionsEntity(
            sourceTargetQuestionRefId: Long,
            conditions: String,
            userId: String?
        ): ConditionsEntity {
            return ConditionsEntity(
                sourceTargetQuestionRefId = sourceTargetQuestionRefId,
                conditions = conditions,
                userId = userId
            )
        }

    }

}
