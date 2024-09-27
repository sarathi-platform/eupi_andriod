package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("conditions_table")
data class ConditionsEntity(

    @PrimaryKey(true)
    @ColumnInfo("id")
    val id: Int = 0,

    val sourceTargetQuestionRefId: Long,
    val conditions: String
) {

    companion object {

        fun getConditionsEntity(
            sourceTargetQuestionRefId: Long,
            conditions: String
        ): ConditionsEntity {
            return ConditionsEntity(
                sourceTargetQuestionRefId = sourceTargetQuestionRefId,
                conditions = conditions
            )
        }

    }

}
