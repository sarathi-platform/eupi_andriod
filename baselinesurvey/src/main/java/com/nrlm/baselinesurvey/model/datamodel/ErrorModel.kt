package com.nrlm.baselinesurvey.model.datamodel

import com.nrlm.baselinesurvey.CODE_SUCCESS
import com.nrlm.baselinesurvey.COMMON_ERROR_MSG
import com.nrlm.baselinesurvey.COMMON_ERROR_TITLE

data class ErrorModel(val code:Int=-1, var message:String?= COMMON_ERROR_MSG, var title:String= COMMON_ERROR_TITLE,
                      var statusCode:Int= CODE_SUCCESS,
                      var isConcurrency : Boolean = false)
