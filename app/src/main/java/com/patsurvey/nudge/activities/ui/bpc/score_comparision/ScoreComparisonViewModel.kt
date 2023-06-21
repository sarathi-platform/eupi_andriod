package com.patsurvey.nudge.activities.ui.bpc.score_comparision

import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.network.model.ErrorModelWithApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScoreComparisonViewModel @Inject constructor(

): BaseViewModel() {





    override fun onServerError(error: ErrorModel?) {
        TODO("Not yet implemented")
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }

}
