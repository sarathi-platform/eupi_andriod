package com.nrlm.baselinesurvey.ui.start_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.EventsWriterUserCase
import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.GetCasteListUseCase
import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.UpdateSurveyStateUserCase
import com.nrlm.baselinesurvey.ui.question_screen.domain.use_case.GetSectionUseCase

data class StartSurveyScreenUserCase(
    val getSurveyeeDetailsUserCase: GetSurveyeeDetailsUserCase,
    val saveSurveyeeImagePathUseCase: SaveSurveyeeImagePathUseCase,
    val updateSurveyStateUseCase: UpdateSurveyStateUserCase,
    val getCasteListUseCase: GetCasteListUseCase,
    val getSectionUseCase: GetSectionUseCase,
    val eventsWriterUserCase: EventsWriterUserCase
)
