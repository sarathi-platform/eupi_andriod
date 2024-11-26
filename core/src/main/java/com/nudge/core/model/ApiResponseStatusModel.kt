package com.nudge.core.model

data class ApiResponseStatusModel(
    var apiEndPoint: String,
    val status: Int,
    val errorMessage: String,
    val errorCode: Int
)
