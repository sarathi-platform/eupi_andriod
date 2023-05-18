package com.patsurvey.nudge.activities

import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PatDidiSummaryViewModel @Inject constructor(val prefRepo: PrefRepo) : BaseViewModel() {

    init {

    }
}