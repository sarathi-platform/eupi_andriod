package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nrlm.baselinesurvey.OPTION_TABLE
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity

@Dao
interface OptionItemDao {
    @Insert
    fun insertOption(optionItem: OptionItemEntity)

    @Query("Delete from $OPTION_TABLE where optionId=:optionId and questionId = :questionId and sectionId = :sectionId and surveyId = :surveyId and languageId = :languageId")
    fun deleteSurveySectionQuestionOptionFroLanguage(
        optionId: Int,
        questionId: Int,
        sectionId: Int,
        surveyId: Int,
        languageId: Int
    )

    @Query("Select * from $OPTION_TABLE where  sectionId = :sectionId and surveyId = :surveyId and languageId = :languageId")
    fun getSurveySectionQuestionOptionForLanguage(
        sectionId: Int,
        surveyId: Int,
        languageId: Int
    ): List<OptionItemEntity>

    @Query("Select * from $OPTION_TABLE where  sectionId = :sectionId and surveyId = :surveyId and questionId = :questionId")
    fun getSurveySectionQuestionOptions(
        sectionId: Int,
        surveyId: Int,
        questionId: Int,
    ): List<OptionItemEntity>

//    @Query("Select * from $OPTION_TABLE where surveyId = :surveyId and languageId = :languageId")
//    fun getAllOptionForLanguage(surveyId: Int, languageId: Int): List<OptionItemEntity>

}