package com.nrlm.baselinesurvey.model.datamodel

import com.nudge.core.datamodel.BaseLineQnATableCSV
import com.nudge.core.model.request.EventRequest

fun SaveAnswerEventDto.toCsv(): BaseLineQnATableCSV {
    val selectedValues = this.question.options.map { it.selectedValue }
    val response = selectedValues.joinToString(", ")
    return BaseLineQnATableCSV(
        id = this.surveyId,
        question = this.question.questionDesc,
        response = response,
        subjectId = subjectId
    )
}

