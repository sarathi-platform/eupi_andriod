package com.sarathi.dataloadingmangement.domain.use_case

import com.nudge.core.SUCCESS
import com.sarathi.dataloadingmangement.network.ApiException
import com.sarathi.dataloadingmangement.repository.IDataLoadingScreenRepository

class FetchUserDetailsUseCase(private val repository: IDataLoadingScreenRepository) {

    suspend operator fun invoke(): Boolean {

        try {
            val response = repository.getUserDetailsFromNetwork("2")
            if (response.status.equals(SUCCESS, true)) {

                response.data?.let {
                    repository.saveUserDetails(it)
                    return true
                } ?: return false
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
