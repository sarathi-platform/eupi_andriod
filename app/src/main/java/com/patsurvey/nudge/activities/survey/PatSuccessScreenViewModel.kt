package com.patsurvey.nudge.activities.survey

import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_STATE_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PatSuccessScreenViewModel @Inject constructor(
    val prefRepo: PrefRepo
): BaseViewModel() {



    override fun onServerError(error: ErrorModel?) {

    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {

    }
    fun getStateId(): Int {
        return prefRepo.getPref(PREF_KEY_TYPE_STATE_ID, 4)
    }
}
