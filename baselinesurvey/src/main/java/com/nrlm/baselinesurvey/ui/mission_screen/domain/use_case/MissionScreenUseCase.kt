package com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case

import javax.inject.Inject


data class MissionScreenUseCase @Inject constructor(val getSectionUseCase: GetSectionsListUseCase)
