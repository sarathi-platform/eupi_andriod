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

    @Query("SELECT * from $SECTION_STATUS_TABLE_NAME where missionId = :missionId and surveyId = :surveyId and taskId = :taskId and userId = :userId")
    suspend fun getStatusForTask(
        missionId: Int,
        surveyId: Int,
        taskId: Int,
        userId: String
    ): List<SectionStatusEntity>?

    @Query("SELECT * from $SECTION_STATUS_TABLE_NAME where missionId = :missionId and surveyId = :surveyId and taskId = :taskId and sectionId = :sectionId and userId = :userId")
    suspend fun getSectionStatusForTask(
        missionId: Int,
        surveyId: Int,
        taskId: Int,
        sectionId: Int,
        userId: String
    ): SectionStatusEntity

    @Query("UPDATE $SECTION_STATUS_TABLE_NAME SET missionId = :missionId and sectionStatus = :sectionStatus where surveyId = :surveyId and taskId = :taskId and sectionId = :sectionId and userId = :userId")
    suspend fun updateSectionStatusForTask(
        missionId: Int,
        surveyId: Int,
        taskId: Int,
        sectionId: Int,
        userId: String,
        sectionStatus: String
    )

    @Query("SELECT COUNT(*) from $SECTION_STATUS_TABLE_NAME where missionId = :missionId and surveyId = :surveyId and taskId = :taskId and sectionId = :sectionId and userId = :userId")
    suspend fun isStatusAvailableForSection(
        missionId: Int,
        surveyId: Int,
        taskId: Int,
        sectionId: Int,
        userId: String
    ): Int

}