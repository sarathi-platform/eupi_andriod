package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.SECTION_TABLE
import com.sarathi.dataloadingmangement.SURVEY_LANGUAGE_ATTRIBUTE_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.SectionEntity
import com.sarathi.dataloadingmangement.model.uiModel.SectionUiModel

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

    @Query("Select sectionTable.sectionId, sectionTable.userId, sectionTable.surveyId, langAttrTable.description as sectionName, sectionTable.sectionOrder, sectionTable.sectionDetails, sectionTable.sectionIcon, sectionTable.questionSize from $SECTION_TABLE as sectionTable join $SURVEY_LANGUAGE_ATTRIBUTE_TABLE_NAME as langAttrTable on sectionTable.sectionId = langAttrTable.referenceId where sectionTable.userId=:userId and sectionTable.surveyId = :surveyId and langAttrTable.userId = :userId and langAttrTable.languageCode = :languageCode and langAttrTable.referenceType = 'SECTION' order by sectionTable.sectionOrder ASC")
    fun getAllSectionForSurveyInLanguage(
        userId: String,
        surveyId: Int,
        languageCode: String
    ): List<SectionUiModel>

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