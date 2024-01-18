package com.nrlm.baselinesurvey.ui.start_screen.presentation

import android.content.Context
import android.net.Uri
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.utils.states.SurveyState

sealed class StartSurveyScreenEvents {

    data class SaveImagePathForSurveyee(val context: Context): StartSurveyScreenEvents()

}