package com.tothenew.android_starter_project.model.response

import com.tothenew.android_starter_project.network.model.ErrorDataModel

class ApiResponseModel(  val data: Any?,
                         val code: Int?,
                         val reqUrl: String?,
                         val throwable: Throwable?,
                         val errorMessage: String? = null,
                         val httpCode: Int = -1,
                         val mError: ErrorDataModel? = null)