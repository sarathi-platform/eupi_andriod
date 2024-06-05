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

    @Query("Select * from $SECTION_TABLE where userId=:userId and sectionId = :sectionId and surveyId = :surveyId and languageId = :languageId")
    fun getSurveySectionForLanguage(
        userId: String,
        sectionId: Int,
        surveyId: Int,
        languageId: Int
    ): SectionEntity

    @Query("Select * from $SECTION_TABLE where userId=:userId and surveyId = :surveyId and languageId = :languageId")
    fun getAllSectionForSurveyInLanguage(
        userId: String,
        surveyId: Int,
        languageId: Int
    ): List<SectionEntity>

    @Query("Delete from $SECTION_TABLE where userId=:userId and sectionId = :sectionId and surveyId = :surveyId and languageId = :languageId")
    fun deleteSurveySectionFroLanguage(
        userId: String,
        sectionId: Int,
        surveyId: Int,
        languageId: String
    )

    @Query("Select * from $SECTION_TABLE where userId=:userId")
    fun getSections(userId: String): List<SectionEntity?>?

    @Query("Select * from $SECTION_TABLE where userId=:userId and languageId = :languageId")
    fun getSectionsT(
        userId: String,
        languageId: Int,
    ): List<SectionEntity>

}