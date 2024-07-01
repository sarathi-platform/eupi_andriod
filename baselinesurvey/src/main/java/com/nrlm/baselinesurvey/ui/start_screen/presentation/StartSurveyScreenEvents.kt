package com.nrlm.baselinesurvey.ui.start_screen.presentation

import android.app.Activity
import android.content.Context
import com.nrlm.baselinesurvey.database.entity.DidiInfoEntity

sealed class StartSurveyScreenEvents {

    data class SaveImagePathForSurveyee(val context: Activity) : StartSurveyScreenEvents()

    data class SaveDidiInfoInDbEvent(val didiInfoEntity: DidiInfoEntity) : StartSurveyScreenEvents()

}