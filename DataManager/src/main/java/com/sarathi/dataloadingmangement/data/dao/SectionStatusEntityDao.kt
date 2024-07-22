package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.SECTION_STATUS_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.SectionStatusEntity

@Dao
interface SectionStatusEntityDao {

    @Insert
    suspend fun addSectionStatus(sectionStatusEntity: SectionStatusEntity)

    @Query("SELECT * from $SECTION_STATUS_TABLE_NAME where surveyId = :surveyId and taskId = :taskId and userId = :userId")
    suspend fun getStatusForTask(
        surveyId: Int,
        taskId: Int,
        userId: String
    ): List<SectionStatusEntity>?

    @Query("SELECT * from $SECTION_STATUS_TABLE_NAME where surveyId = :surveyId and taskId = :taskId and sectionId = :sectionId and userId = :userId")
    suspend fun getSectionStatusForTask(
        surveyId: Int,
        taskId: Int,
        sectionId: Int,
        userId: String
    ): List<SectionStatusEntity>

    @Query("UPDATE $SECTION_STATUS_TABLE_NAME SET sectionStatus = :sectionStatus where surveyId = :surveyId and taskId = :taskId and sectionId = :sectionId and userId = :userId")
    suspend fun updateSectionStatusForTask(
        surveyId: Int,
        taskId: Int,
        sectionId: Int,
        userId: String,
        sectionStatus: String
    )

}