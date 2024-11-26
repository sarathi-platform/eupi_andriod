package com.nudge.core.data.repository

import com.nudge.core.BLANK_STRING
import com.nudge.core.database.entities.ApiStatusEntity
import com.nudge.core.enums.ApiDetails
import com.nudge.core.enums.ApiStatus
import com.nudge.core.enums.CallScreen

interface ApiStatusEntityRepository {

    fun insertOrUpdateApiStatus(
        apiDetails: ApiDetails,
        status: Int = ApiStatus.INPROGRESS.ordinal,
        errorMessage: String = BLANK_STRING,
        errorCode: Int = 0,
        callScreen: CallScreen
    )

    fun insertApiStatus(apiDetails: ApiDetails, callScreen: CallScreen)

    fun updateApiStatus(
        apiDetails: ApiDetails, status: Int,
        errorMessage: String,
        errorCode: Int, callScreen: CallScreen
    )

    fun getApiStatusEntity(apiEndpoint: String): ApiStatusEntity?

    fun checkAndUpdateCallScreenForApiEndpoint(apiEndpoint: String, callScreen: CallScreen)

    fun isApiStatusEntryAvailable(apiEndpoint: String): Int
}