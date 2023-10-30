package com.nrlm.baselinesurvey.ui.question_screen.domain.repository

import com.nrlm.baselinesurvey.model.datamodel.Sections

interface QuestionScreenRepository {

    fun getSections(sectionId: Int): Sections

}
