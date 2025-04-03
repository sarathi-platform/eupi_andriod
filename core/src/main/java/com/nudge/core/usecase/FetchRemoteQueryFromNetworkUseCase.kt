package com.nudge.core.usecase

import com.google.android.gms.common.api.ApiException
import com.nudge.core.SUCCESS
import com.nudge.core.data.repository.RemoteQueryAuditTrailRepository
import com.nudge.core.data.repository.RemoteQueryNetworkRepository
import javax.inject.Inject

class FetchRemoteQueryFromNetworkUseCase @Inject constructor(
    private val remoteQueryNetworkRepository: RemoteQueryNetworkRepository,
    private val remoteQueryAuditTrailRepository: RemoteQueryAuditTrailRepository
) {

    suspend fun invoke(): Boolean {
        try {
            val apiResponse = remoteQueryNetworkRepository.fetchRemoteQueryFromNetwork()

            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    remoteQueryAuditTrailRepository.saveRemoteQueryToDb(apiResponse.data)
                }
                return true
            } else {
                return false
            }

        } catch (apiException: ApiException) {
            throw apiException
        } catch (ex: Exception) {
            throw ex
        }
    }
}

