package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nrlm.baselinesurvey.SECTION_TABLE
import com.nrlm.baselinesurvey.database.entity.SectionEntity

@Dao
interface SectionEntityDao {

    @Insert
    fun insertSection(sectionEntity: SectionEntity)

    @Query("Select * from $SECTION_TABLE where userId=:userId and sectionId = :sectionId and surveyId = :surveyId and languageId = :languageId")
    fun getSurveySectionForLanguage(
        userId: Int,
        sectionId: Int,
        surveyId: Int,
        languageId: Int
    ): SectionEntity

    @Query("Select * from $SECTION_TABLE where  userId=:userId and surveyId = :surveyId and languageId = :languageId")
    fun getAllSectionForSurveyInLanguage(
        userId: Int,
        surveyId: Int,
        languageId: Int
    ): List<SectionEntity>

    @Query("Delete from $SECTION_TABLE where userId=:userId and sectionId = :sectionId and surveyId = :surveyId and languageId = :languageId")
    fun deleteSurveySectionFroLanguage(userId: Int, sectionId: Int, surveyId: Int, languageId: Int)

    @Query("Select * from $SECTION_TABLE where userId=:userId")
    fun getSections(userId: Int): List<SectionEntity?>?


}