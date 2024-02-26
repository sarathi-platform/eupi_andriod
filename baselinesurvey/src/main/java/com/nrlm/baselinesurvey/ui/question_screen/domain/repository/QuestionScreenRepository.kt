package com.nrlm.baselinesurvey.ui.question_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.InputTypeQuestionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.SectionAnswerEntity
import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.utils.states.SectionStatus

interface QuestionScreenRepository {

    suspend fun getSections(sectionId: Int, surveyId: Int, languageId: Int): SectionListItem

    fun getSelectedLanguage(): Int
    suspend fun updateSectionProgress(surveyId: Int, sectionId: Int, didiId: Int, sectionStatus: SectionStatus)

    fun saveSectionAnswerForDidi(
        didiId: Int,
        sectionId: Int,
        questionId: Int,
        surveyId: Int,
        optionItems: List<OptionItemEntity>,
        questionType: String,
        questionSummary: String
    )

    fun updateSectionAnswerForDidi(
        didiId: Int,
        sectionId: Int,
        questionId: Int,
        surveyId: Int,
        optionItems: List<OptionItemEntity>,
        questionType: String,
        questionSummary: String
    )

    fun isQuestionAlreadyAnswered(didiId: Int, questionId: Int, sectionId: Int, surveyId: Int): Int

    fun isInputTypeQuestionAlreadyAnswered(surveyId: Int, sectionId: Int, didiId: Int, questionId: Int, optionItemId: Int): Int

    fun getAllAnswersForDidi(didiId: Int): List<SectionAnswerEntity>

    fun getSectionAnswerForDidi(sectionId: Int, didiId: Int): List<SectionAnswerEntity>

    suspend fun saveSectionAnswersToServer(didiId: Int, surveyId: Int)

    suspend fun updateDidiSurveyStatus(didiId: Int, surveyId: Int)

    suspend fun getSectionsList(surveyId: Int, languageId: Int): List<SectionEntity>

    suspend fun updateInputTypeQuestionAnswer(surveyId: Int, sectionId: Int, questionId: Int, didiId: Int, optionId: Int, inputValue: String)

    suspend fun saveInputTypeQuestionAnswer(surveyId: Int, sectionId: Int, questionId: Int, didiId: Int, optionId: Int, inputValue: String)

    suspend fun getAllInputTypeQuestionAnswersForDidi(surveyId: Int, sectionId: Int, didiId: Int): List<InputTypeQuestionAnswerEntity>

    /*suspend fun updateOptionItem(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        optionItem: OptionItemEntity,
    )

    suspend fun updateOptionItemValue(
        surveyId: Int,
        sectionId: Int,
        questionId: Int,
        optionId: Int,
        selectedValue: String
    )*/

}
