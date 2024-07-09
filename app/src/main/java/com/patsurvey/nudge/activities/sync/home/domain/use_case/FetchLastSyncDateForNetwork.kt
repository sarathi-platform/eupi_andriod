package com.patsurvey.nudge.activities.sync.home.domain.use_case

import com.google.android.gms.common.api.ApiException
import com.patsurvey.nudge.activities.sync.home.domain.repository.SyncHomeRepository
import com.sarathi.dataloadingmangement.SUCCESS
import javax.inject.Inject

class FetchLastSyncDateForNetwork @Inject constructor(
    private val syncHomeRepository: SyncHomeRepository
) {
    suspend fun invoke(): Boolean {
        try {
            val apiResponse = syncHomeRepository.getLastSyncDateTime()
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.lastSyncDate?.let {
                    if (it > 0) {
                        syncHomeRepository.saveLastSyncDateTime(it)
                    }
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