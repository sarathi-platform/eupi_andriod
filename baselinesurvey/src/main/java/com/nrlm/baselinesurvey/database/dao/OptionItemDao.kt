package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nrlm.baselinesurvey.ANSWER_TABLE
import com.nrlm.baselinesurvey.OPTION_TABLE
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity

@Dao
interface OptionItemDao {
    @Insert
    fun insertOption(optionItem: OptionItemEntity)

    @Query("Delete from $OPTION_TABLE where  userId=:userId and optionId=:optionId and questionId = :questionId and sectionId = :sectionId and surveyId = :surveyId and languageId = :languageId")
    fun deleteSurveySectionQuestionOptionFroLanguage(
        userId: String,
        optionId: Int,
        questionId: Int,
        sectionId: Int,
        surveyId: Int,
        languageId: Int
    )

    @Query("Select * from $OPTION_TABLE where  userId=:userId and  sectionId = :sectionId and surveyId = :surveyId and languageId = :languageId")
    fun getSurveySectionQuestionOptionForLanguage(
        userId: String,
        sectionId: Int,
        surveyId: Int,
        languageId: Int
    ): List<OptionItemEntity>

    @Query("Select * from $OPTION_TABLE where  userId=:userId and  sectionId = :sectionId and surveyId = :surveyId and questionId = :questionId and languageId=:languageId")
    fun getSurveySectionQuestionOptions(
        userId: String,
        sectionId: Int,
        surveyId: Int,
        questionId: Int,
        languageId: Int
    ): List<OptionItemEntity>

    @Query("Update $OPTION_TABLE set isSelected = :isSelected where userId=:userId and  surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId AND optionId = :optionId")
    fun updateOptionItem(
        userId: String,
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        optionId: Int,
        isSelected: Boolean,
    )

    @Query("Update $OPTION_TABLE set selectedValue = :selectValue where  userId=:userId and surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId AND optionId = :optionId")
    fun updateOptionItemValue(
        userId: String,
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        optionId: Int,
        selectValue: String,
    )
//    @Query("Select * from $OPTION_TABLE where surveyId = :surveyId and languageId = :languageId")
//    fun getAllOptionForLanguage(surveyId: Int, languageId: Int): List<OptionItemEntity>


    @Query("Select * from $OPTION_TABLE where userId=:userId and  surveyId = :surveyId and languageId = :languageId")
    fun getAllOptionForLanguage(
        userId: String,
        surveyId: Int,
        languageId: Int
    ): List<OptionItemEntity>

    @Query("Select COUNT(*) FROM $ANSWER_TABLE where userId=:userId and  questionId = :questionId AND sectionId = :sectionId AND surveyId = :surveyId")
    fun isOptionAlreadyPresent(userId: String, questionId: Int, sectionId: Int, surveyId: Int): Int

    @Query("Delete from $OPTION_TABLE where userId=:userId")
    fun deleteOptions(userId: String)
}