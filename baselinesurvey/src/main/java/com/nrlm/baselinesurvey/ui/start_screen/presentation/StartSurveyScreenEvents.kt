package com.nrlm.baselinesurvey.ui.start_screen.presentation

import android.content.Context
import com.nrlm.baselinesurvey.database.entity.DidiInfoEntity

sealed class StartSurveyScreenEvents {

    data class SaveImagePathForSurveyee(val context: Context) : StartSurveyScreenEvents()

    data class SaveDidiInfoInDbEvent(val didiInfoEntity: DidiInfoEntity) : StartSurveyScreenEvents()

}