package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sarathi.dataloadingmangement.SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME

@Entity(tableName = SUBJECT_LIVELIHOOD_MAPPING_TABLE_NAME)
data class SubjectLivelihoodMappingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    val userId: String,
    val subjectId: Int,
    val primaryLivelihoodId: Int,
    val secondaryLivelihoodId: Int,
    val livelihoodId: Int,
    val type: Int,
    val status:Int
) {
    companion object {
        fun getSubjectLivelihoodMappingEntity(
            userId: String,
            subjectId: Int,
            primaryLivelihoodId: Int,
            secondaryLivelihoodId: Int,
             livelihoodId: Int,
             type: Int,
             status:Int
        ): SubjectLivelihoodMappingEntity {
            return SubjectLivelihoodMappingEntity(
                userId = userId,
                subjectId = subjectId,
                primaryLivelihoodId = primaryLivelihoodId,
                livelihoodId = livelihoodId,
                type = type,
                status = status,
                secondaryLivelihoodId = secondaryLivelihoodId
            )
        }
    }
}
