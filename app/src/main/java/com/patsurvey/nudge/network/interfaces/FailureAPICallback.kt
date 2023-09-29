package com.patsurvey.nudge.network.interfaces

import com.patsurvey.nudge.network.model.ErrorDataModel

interface FailureAPICallback {

    fun onFailure(
        code: Int,
        reqUrl: String = "",
        throwable: Throwable?,
        errorMessage: String? = null,
        httpCode: Int = -1,
        mError: ErrorDataModel? = null)
}
