package com.patsurvey.nudge.model.response

import com.patsurvey.nudge.network.model.ErrorDataModel

class ApiResponseModel(  val data: Any?,
                         val code: Int?,
                         val reqUrl: String?,
                         val throwable: Throwable?,
                         val errorMessage: String? = null,
                         val httpCode: Int = -1,
                         val mError: ErrorDataModel? = null)