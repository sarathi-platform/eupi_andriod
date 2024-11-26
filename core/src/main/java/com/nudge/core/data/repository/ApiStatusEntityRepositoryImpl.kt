package com.nudge.core.data.repository

import com.nudge.core.BLANK_STRING
import com.nudge.core.database.dao.ApiStatusDao
import com.nudge.core.database.entities.ApiStatusEntity
import com.nudge.core.enums.ApiDetails
import com.nudge.core.enums.ApiStatus
import com.nudge.core.enums.CallScreen
import com.nudge.core.getCurrentTimeInMillis
import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDate
import javax.inject.Inject

class ApiStatusEntityRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val apiStatusDao: ApiStatusDao
) : ApiStatusEntityRepository {

    override fun insertOrUpdateApiStatus(
        apiDetails: ApiDetails,
        status: Int,
        errorMessage: String,
        errorCode: Int,
        callScreen: CallScreen
    ) {
        val isApiStatusEntryAvailable = isApiStatusEntryAvailable(apiDetails.endPoint)
        if (isApiStatusEntryAvailable > 0) {
            updateApiStatus(apiDetails, status, errorMessage, errorCode, callScreen)
        } else {
            insertApiStatus(apiDetails, callScreen)
        }
    }

    override fun insertApiStatus(apiDetails: ApiDetails, callScreen: CallScreen) {
        val apiEntity = ApiStatusEntity(
            apiEndpoint = apiDetails.endPoint,
            status = ApiStatus.INPROGRESS.ordinal,
            errorCode = 0,
            errorMessage = BLANK_STRING,
            createdDate = getCurrentTimeInMillis().toDate(),
            modifiedDate = getCurrentTimeInMillis().toDate(),
            callScreen = listOf(callScreen.name)
        )
    }

    override fun updateApiStatus(
        apiDetails: ApiDetails,
        status: Int,
        errorMessage: String,
        errorCode: Int,
        callScreen: CallScreen
    ) {
        apiStatusDao.updateApiStatus(
            apiEndpoint = apiDetails.endPoint, status = status,
            errorMessage = errorMessage,
            errorCode = errorCode
        )
        checkAndUpdateCallScreenForApiEndpoint(apiEndpoint = apiDetails.endPoint, callScreen)
    }

    override fun getApiStatusEntity(apiEndpoint: String): ApiStatusEntity? {
        return apiStatusDao.getAPIStatus(apiEndpoint)
    }

    override fun checkAndUpdateCallScreenForApiEndpoint(
        apiEndpoint: String,
        callScreen: CallScreen
    ) {
        val apiStatusEntity = getApiStatusEntity(apiEndpoint)
        apiStatusEntity?.let {
            if (!it.callScreen.contains(callScreen.name)) {
                val updatedCallScreenList = it.callScreen.toMutableList()
                updatedCallScreenList.add(callScreen.name)
                apiStatusDao.updateCallScreenForApiEndpoint(apiEndpoint, updatedCallScreenList)
            }
        }
    }

    override fun isApiStatusEntryAvailable(apiEndpoint: String): Int {
        return apiStatusDao.isApiStatusEntryAvailable(apiEndpoint)
    }
}