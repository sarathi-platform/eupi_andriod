package com.sarathi.surveymanager.utils.events

import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.entities.OptionItemEntity

sealed class EventWriterEvents {
    data class SaveAnswerEvent(
        val userId: String,
        val questionId: Int,
        val subjectId: Int,
        val surveyId: Int,
        val sectionId: Int,
        val referenceId: Int,
        var answerValue: String,
        val questionType: String,
        val taskId: Int,
        var optionItems: List<OptionItemEntity>,
        val questionSummary: String? = BLANK_STRING,
        var needsToPost: Boolean = true,
    )

}