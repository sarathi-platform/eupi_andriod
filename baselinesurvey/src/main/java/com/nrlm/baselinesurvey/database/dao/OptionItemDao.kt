package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nrlm.baselinesurvey.ANSWER_TABLE
import com.nrlm.baselinesurvey.OPTION_TABLE
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem

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

    @Query("Update $OPTION_TABLE set isSelected = :isSelected where surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId AND optionId = :optionId")
    fun updateOptionItem(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        optionId: Int,
        isSelected: Boolean,
    )

    @Query("Update $OPTION_TABLE set selectedValue = :selectValue where surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId AND optionId = :optionId")
    fun updateOptionItemValue(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        optionId: Int,
        selectValue: String,
    )
//    @Query("Select * from $OPTION_TABLE where surveyId = :surveyId and languageId = :languageId")
//    fun getAllOptionForLanguage(surveyId: Int, languageId: Int): List<OptionItemEntity>


    @Query("Select * from $OPTION_TABLE where surveyId = :surveyId and languageId = :languageId")
    fun getAllOptionForLanguage(surveyId: Int, languageId: Int): List<OptionItemEntity>

    @Query("Select COUNT(*) FROM $ANSWER_TABLE where questionId = :questionId AND sectionId = :sectionId AND surveyId = :surveyId")
    fun isOptionAlreadyPresent(questionId: Int, sectionId: Int, surveyId: Int): Int

}