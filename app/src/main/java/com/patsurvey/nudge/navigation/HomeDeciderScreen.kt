package com.patsurvey.nudge.navigation

import androidx.compose.runtime.Composable
import com.nrlm.baselinesurvey.BLANK_STRING
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.navigation.baseline.BSHomeNavScreen
import com.patsurvey.nudge.navigation.selection.HomeNavScreen
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_NAME
import com.patsurvey.nudge.utils.UPCM_USER

@Composable
fun HomeDeciderScreen( prefRepo: PrefRepo){
    if(prefRepo.getPref(PREF_KEY_TYPE_NAME, BLANK_STRING).equals(UPCM_USER)){
        BSHomeNavScreen(prefRepo=prefRepo)
    }else {
        HomeNavScreen(prefRepo = prefRepo)
    }
}