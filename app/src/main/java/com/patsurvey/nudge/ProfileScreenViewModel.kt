package com.patsurvey.nudge

import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    val prefRepo: PrefRepo
): BaseViewModel() {

    override fun onServerError(error: ErrorModel?) {
        TODO("Not yet implemented")
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }

}
