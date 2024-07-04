package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.SECTION_TABLE
import com.sarathi.dataloadingmangement.data.entities.SectionEntity

@Dao
interface SectionEntityDao {

    @Insert
    fun insertSection(sectionEntity: SectionEntity)

    @Query("Select * from $SECTION_TABLE where userId=:userId and sectionId = :sectionId and surveyId = :surveyId ")
    fun getSurveySectionForLanguage(
        userId: String,
        sectionId: Int,
        surveyId: Int,
    ): SectionEntity

    @Query("Select * from $SECTION_TABLE where userId=:userId and surveyId = :surveyId ")
    fun getAllSectionForSurveyInLanguage(
        userId: String,
        surveyId: Int,
    ): List<SectionEntity>

    @Query("Delete from $SECTION_TABLE where userId=:userId and sectionId = :sectionId and surveyId = :surveyId ")
    fun deleteSurveySectionFroLanguage(
        userId: String,
        sectionId: Int,
        surveyId: Int,
    )

    @Query("Select * from $SECTION_TABLE where userId=:userId")
    fun getSections(userId: String): List<SectionEntity?>?

    @Query("Select * from $SECTION_TABLE where userId=:userId ")
    fun getSectionsT(
        userId: String,
    ): List<SectionEntity>

    @Query("Delete from $SECTION_TABLE where userId=:userId ")
    fun deleteSurveySectionsForUser(
        userId: String,
    )
}