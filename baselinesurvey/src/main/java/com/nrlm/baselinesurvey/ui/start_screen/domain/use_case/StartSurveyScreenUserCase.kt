package com.nrlm.baselinesurvey.ui.start_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.common_components.common_domain.common_use_case.UpdateSurveyStateUserCase

data class StartSurveyScreenUserCase(
    val getSurveyeeDetailsUserCase: GetSurveyeeDetailsUserCase,
    val saveSurveyeeImagePathUseCase: SaveSurveyeeImagePathUseCase,
    val updateSurveyStateUseCase: UpdateSurveyStateUserCase
)
