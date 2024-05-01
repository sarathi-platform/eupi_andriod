package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_ERROR_CODE
import com.nrlm.baselinesurvey.DEFAULT_SUCCESS_CODE
import com.nrlm.baselinesurvey.SUCCESS_CODE
import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.model.mappers.ContentEntityMapper
import com.nrlm.baselinesurvey.model.request.ContentMangerRequest
import com.nrlm.baselinesurvey.network.ApiException
import com.nrlm.baselinesurvey.network.SUBPATH_CONTENT_MANAGER
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nudge.core.enums.ApiStatus

class FetchContentDataFromNetworkUseCase(private val repository: DataLoadingScreenRepository) {
    suspend operator fun invoke(): Boolean {
        try {
            //TODO Run a loop on language id later
            if (!repository.isNeedToCallApi(SUBPATH_CONTENT_MANAGER)) {
                return false
            }
            repository.insertApiStatus(SUBPATH_CONTENT_MANAGER)

            val contentKeys =
                repository.getContentKeyFromDB().filter { it -> it != "" && it != "question" }
            val languageId = repository.getSelectedLanguageId()
            val contentEntities = mutableListOf<ContentEntity>()
            val contentMangerRequest = ContentMangerRequest(languageId, contentKeys)
            val contentResponse = repository.fetchContentsFromServer(contentMangerRequest)
            if (contentResponse.status.equals(
                    SUCCESS_CODE,
                    true
                ) || contentResponse.status.equals("SUCCESS", true)
            ) {
                contentResponse.data?.let { content ->
                    repository.updateApiStatus(
                        SUBPATH_CONTENT_MANAGER,
                        status = ApiStatus.SUCCESS.ordinal,
                        BLANK_STRING,
                        DEFAULT_SUCCESS_CODE
                    )
                    repository.deleteContentFromDB()
                    for (content in contentResponse.data) {
                        if (content != null) {
                            contentEntities.add(ContentEntityMapper.getContentEntity(content))
                        }
                    }
                    repository.saveContentsToDB(contentEntities)
                    return true
                }
                return false
            } else {
                repository.updateApiStatus(
                    SUBPATH_CONTENT_MANAGER,
                    status = ApiStatus.FAILED.ordinal,
                    contentResponse.message,
                    DEFAULT_ERROR_CODE
                )
                return false
            }
        } catch (apiException: ApiException) {
            repository.updateApiStatus(
                SUBPATH_CONTENT_MANAGER,
                status = ApiStatus.FAILED.ordinal,
                apiException.message ?: BLANK_STRING,
                apiException.getStatusCode()
            )
            throw apiException
        } catch (ex: Exception) {
            repository.updateApiStatus(
                SUBPATH_CONTENT_MANAGER,
                status = ApiStatus.FAILED.ordinal,
                ex.message ?: BLANK_STRING,
                DEFAULT_ERROR_CODE
            )
            BaselineLogger.e("FetchUserDetailFromNetworkUseCase", "invoke", ex)
            throw ex
        }
    }
}