package com.patsurvey.nudge.network.model

import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.CODE_SUCCESS
import com.patsurvey.nudge.utils.COMMON_ERROR_MSG
import com.patsurvey.nudge.utils.COMMON_ERROR_TITLE

data class ErrorModel(val code:Int=-1, var message:String?= COMMON_ERROR_MSG, var title:String= COMMON_ERROR_TITLE,
                      var statusCode:Int= CODE_SUCCESS,
                      var isConcurrency : Boolean = false)

data class ErrorModelWithApi(val code:Int=-1, var apiName: ApiType, var message:String?= COMMON_ERROR_MSG, var title:String= COMMON_ERROR_TITLE,
                      var statusCode:Int= CODE_SUCCESS,
                      var isConcurrency : Boolean = false)