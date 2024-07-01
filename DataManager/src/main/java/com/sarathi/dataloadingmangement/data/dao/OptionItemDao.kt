package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sarathi.dataloadingmangement.OPTION_TABLE
import com.sarathi.dataloadingmangement.data.entities.OptionItemEntity
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel

@Dao
interface OptionItemDao {
    @Insert
    fun insertOption(optionItem: OptionItemEntity)

    @Query("Delete from $OPTION_TABLE where  userId=:userId and optionId=:optionId and questionId = :questionId and sectionId = :sectionId and surveyId = :surveyId and userId=:userId")
    fun deleteSurveySectionQuestionOptionFroLanguage(
        userId: String,
        optionId: Int,
        questionId: Int,
        sectionId: Int,
        surveyId: Int,
    )

    @Query(
        "select option_table.optionId, \n" +
                " option_table.sectionId,\n" +
                " option_table.surveyId,\n" +
                " option_table.questionId,\n" +
                " option_table.selectedValue,\n" +
                " option_table.optionType,\n" +
                " option_table.`order`,\n" +
                " option_table.contentEntities,\n" +
                " option_table.conditions,\n" +
                " option_table.selectedValue,\n" +
                " option_table.optionTag,\n" +
                " option_table.selectedValueId,\n" +
                " survey_language_attribute_table.description,\n" +
                " survey_language_attribute_table.paraphrase\n" +
                "  from option_table inner join survey_language_attribute_table on option_table.optionId = survey_language_attribute_table.referenceId where survey_language_attribute_table.referenceType =:referenceType \n" +
                "and survey_language_attribute_table.languageCode=:languageId and option_table.surveyId=:surveyId and option_table.sectionId=:sectionId and option_table.userId=:userId  order by option_table.`order` asc  "
    )
    fun getSurveySectionQuestionOptionsForLanguage(
        userId: String,
        sectionId: Int,
        surveyId: Int,
        referenceType: String,
        languageId: String
    ): List<OptionsUiModel>



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



    @Query("Delete from $OPTION_TABLE where userId=:userId")
    fun deleteOptionsForUser(userId: String)

}