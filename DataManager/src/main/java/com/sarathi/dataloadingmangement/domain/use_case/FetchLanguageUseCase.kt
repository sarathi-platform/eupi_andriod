package com.sarathi.dataloadingmangement.domain.use_case

import com.google.android.gms.common.api.ApiException
import com.sarathi.dataloadingmangement.SUCCESS
import com.sarathi.dataloadingmangement.repository.ILanguageRepository
import javax.inject.Inject

class FetchLanguageUseCase @Inject constructor(
    private val repository: ILanguageRepository
) {
    suspend fun invoke(): Boolean {
        try {
            val apiResponse = repository.fetchLanguageDataFromServer()
            if (apiResponse.status.equals(SUCCESS, true)) {
                apiResponse.data?.let {
                    if (apiResponse.data?.languageList?.isNotEmpty() == true) {
                        repository.saveLanguageData(apiResponse.data?.languageList!!)
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