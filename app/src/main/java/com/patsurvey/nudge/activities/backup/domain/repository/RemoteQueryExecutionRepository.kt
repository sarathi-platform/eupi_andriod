package com.patsurvey.nudge.activities.backup.domain.repository

import com.patsurvey.nudge.model.dataModel.RemoteQueryDto

interface RemoteQueryExecutionRepository {

    fun checkIfQueryIsValid(query: String): Boolean

    suspend fun getRemoteQuery(): RemoteQueryDto?

}