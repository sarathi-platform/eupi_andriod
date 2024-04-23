package com.nrlm.baselinesurvey.model.datamodel

import com.nrlm.baselinesurvey.database.entity.SectionEntity
import com.nudge.core.datamodel.BaseLineQnATableCSV

fun List<SectionEntity>.toCsv() : List<BaseLineQnATableCSV> = map {
    BaseLineQnATableCSV(
        id = it.id,
        surveyId = it.id

    )
}
