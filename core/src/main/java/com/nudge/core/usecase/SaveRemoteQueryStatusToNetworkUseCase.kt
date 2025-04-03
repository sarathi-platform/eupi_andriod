package com.nudge.core.usecase

import com.google.android.gms.common.api.ApiException
import com.nudge.core.SUCCESS
import com.nudge.core.data.repository.RemoteQueryNetworkRepository
import com.nudge.core.model.request.RemoteSqlQueryApiRequest
import com.nudge.core.utils.CoreLogger
import javax.inject.Inject

class SaveRemoteQueryStatusToNetworkUseCase @Inject constructor(
    private val remoteQueryNetworkRepository: RemoteQueryNetworkRepository,

    ) {

    suspend fun invoke(apiRequests: ArrayList<RemoteSqlQueryApiRequest>): Boolean {
        try {

            val apiResponse =
                remoteQueryNetworkRepository.saveRemoteQueryStatusToNetwork(apiRequest = apiRequests)

            if (apiResponse.status.equals(SUCCESS, true)) {
                CoreLogger.i(
                    tag = "SaveRemoteQueryStatusToNetworkUseCase",
                    msg = "Request: ${apiRequests.toString()} \n Response: ${apiResponse.data}"
                )
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

