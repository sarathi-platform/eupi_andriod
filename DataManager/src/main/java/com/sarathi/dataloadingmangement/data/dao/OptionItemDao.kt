package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.OPTION_TABLE
import com.sarathi.dataloadingmangement.data.entities.OptionItemEntity

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
    fun getSurveySectionQuestionOptionsForLanguage(
        userId: String,
        sectionId: Int,
        surveyId: Int,
        languageId: Int
    ): List<OptionItemEntity>

    @Query("Select * from $OPTION_TABLE where userId=:userId and sectionId = :sectionId and surveyId = :surveyId and questionId = :questionId and optionId = :optionId and languageId = :languageId")
    fun getSurveySectionQuestionOptionForLanguage(
        userId: String,
        sectionId: Int,
        surveyId: Int,
        questionId: Int,
        optionId: Int,
        languageId: Int
    ): OptionItemEntity?

    @Query("Select * from $OPTION_TABLE where  userId=:userId and sectionId = :sectionId and surveyId = :surveyId and questionId = :questionId and languageId=:languageId")
    fun getSurveySectionQuestionOptions(
        userId: String,
        sectionId: Int,
        surveyId: Int,
        questionId: Int,
        languageId: Int
    ): List<OptionItemEntity>

    @Query("Update $OPTION_TABLE set isSelected = :isSelected where  userId=:userId and surveyId=:surveyId AND sectionId=:sectionId AND questionId = :questionId AND optionId = :optionId")
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

    @Query("Select * from $OPTION_TABLE where userId=:userId and  surveyId = :surveyId and languageId = :languageId")
    fun getAllOptionForLanguage(
        userId: String,
        surveyId: Int,
        languageId: Int
    ): List<OptionItemEntity>


    @Query("Delete from $OPTION_TABLE where userId=:userId")
    fun deleteOptions(userId: String)

    @Query("SELECT * FROM option_table WHERE languageId = :languageId AND optionId IN (:optionIds) AND questionId = :questionId AND sectionId = :sectionId AND surveyId = :surveyId and userId =:userId")
    fun getOptions(
        languageId: Int,
        optionIds: List<Int>,
        questionId: Int,
        sectionId: Int,
        surveyId: Int,
        userId: String
    ): List<OptionItemEntity>
}