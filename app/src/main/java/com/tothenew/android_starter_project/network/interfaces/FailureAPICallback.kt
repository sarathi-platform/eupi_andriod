package com.tothenew.android_starter_project.network.interfaces

import com.tothenew.android_starter_project.network.model.ErrorDataModel

interface FailureAPICallback {

    fun onFailure(
        code: Int,
        reqUrl: String = "",
        throwable: Throwable?,
        errorMessage: String? = null,
        httpCode: Int = -1,
        mError: ErrorDataModel? = null)
}
