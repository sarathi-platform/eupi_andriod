package com.patsurvey.nudge.activities.ui.bpc

import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.database.BpcNonSelectedDidiEntity

object ReplaceHelper {

    var didiToBeReplaced = mutableStateOf(Pair(-1, -1))
    var didiForReplacement: BpcNonSelectedDidiEntity? = null

}