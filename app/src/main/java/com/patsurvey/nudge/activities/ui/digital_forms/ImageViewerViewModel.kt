package com.patsurvey.nudge.activities.ui.digital_forms

import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.network.model.ErrorModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewerViewModel @Inject constructor(
    val prefRepo: PrefRepo
): BaseViewModel() {


    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

}
