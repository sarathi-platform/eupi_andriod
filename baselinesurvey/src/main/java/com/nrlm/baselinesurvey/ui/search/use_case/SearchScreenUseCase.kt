package com.nrlm.baselinesurvey.ui.search.use_case

import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.GetSectionUseCase
import com.nrlm.baselinesurvey.ui.section_screen.domain.use_case.GetSectionListUseCase

data class SearchScreenUseCase(
//    val searchUseCase: SearchUseCase,
    val getSectionListForSurveyUseCase: GetSectionListForSurveyUseCase
)
