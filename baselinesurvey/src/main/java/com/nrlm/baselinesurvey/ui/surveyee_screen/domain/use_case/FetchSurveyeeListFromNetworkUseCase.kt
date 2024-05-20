package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.DEFAULT_ERROR_CODE
import com.nrlm.baselinesurvey.DEFAULT_SUCCESS_CODE
import com.nrlm.baselinesurvey.NO_TOLA_TITLE
import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.network.ApiException
import com.nrlm.baselinesurvey.network.SUBPATH_GET_DIDI_LIST
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.states.SurveyState
import com.nrlm.baselinesurvey.utils.toCamelCase
import com.nudge.core.enums.ApiStatus

class FetchSurveyeeListFromNetworkUseCase(
    private val repository: DataLoadingScreenRepository
) {

    suspend operator fun invoke(): Boolean {
        try {


            if (!repository.isNeedToCallApi(SUBPATH_GET_DIDI_LIST)) {
                return false
            }
            repository.insertApiStatus(SUBPATH_GET_DIDI_LIST)

        val userId = repository.getUserId()
        val apiResponse = repository.fetchSurveyeeListFromNetwork(userId)
        val localSurveyeeEntityList = repository.fetchSurveyeeListFromLocalDb()
        if (apiResponse?.status?.equals(SUCCESS, false) == true) {
            if (apiResponse?.data?.didiList != null) {
                repository.updateApiStatus(
                    SUBPATH_GET_DIDI_LIST,
                    status = ApiStatus.SUCCESS.ordinal,
                    BLANK_STRING,
                    DEFAULT_SUCCESS_CODE
                )
                apiResponse?.data?.didiList.forEach {
                    if (!localSurveyeeEntityList.map { surveyeeEntity -> surveyeeEntity.didiId }.contains(it.didiId)) { //TODO Modify this if to keep backend changes as well
                        val taskForSubject = repository.getTaskForSubjectId(it.didiId)
                        val surveyeeEntity = SurveyeeEntity(
                            id = 0,
                            userId = repository.getBaseLineUserId(),
                            didiId = it.didiId,
                            didiName = it.didiName ?: BLANK_STRING,
                            dadaName = it.dadaName ?: BLANK_STRING,
                            casteId = it.casteId ?: -1,
                            cohortId = it.cohortId ?: -1,
                            cohortName = it.cohortName ?: BLANK_STRING,
                            houseNo = it.houseNo ?: BLANK_STRING,
                            villageId = it.villageId ?: -1,
                            villageName = it.villageName ?: BLANK_STRING,
                            ableBodied = it.ableBodied ?: BLANK_STRING,
                            voName = it.voName ?: BLANK_STRING,
                            surveyStatus = SurveyState.toInt(
                                taskForSubject?.status ?: SurveyState.NOT_STARTED.name
                            )
                        )
                        repository.saveSurveyeeList(surveyeeEntity)
                    }

                }

                apiResponse.data?.didiList?.distinctBy { it.cohortId }?.forEach {
                    if (!localSurveyeeEntityList.map { surveyeeEntity -> surveyeeEntity.didiId }.contains(it.cohortId)) {
                        val taskForSubject = repository.getTaskForSubjectId(it.cohortId)
                        val hamletSurveyEntity = SurveyeeEntity(
                            id = 0,
                            userId = repository.getBaseLineUserId(),
                            didiId = it.cohortId ?: -1,
                            didiName = if (it.cohortName?.equals(
                                    NO_TOLA_TITLE,
                                    true
                                ) == true
                            ) it.villageName?.toCamelCase() ?: BLANK_STRING else it.cohortName
                                ?: BLANK_STRING,
                            dadaName = BLANK_STRING,
                            casteId = -1,
                            cohortId = it.cohortId ?: -1,
                            cohortName = it.villageName ?: BLANK_STRING,
                            houseNo = BLANK_STRING,
                            villageId = it.villageId ?: -1,
                            voName = it.voName ?: BLANK_STRING,
                            villageName = it.villageName ?: BLANK_STRING,
                            ableBodied = BLANK_STRING,
                            surveyStatus = SurveyState.toInt(
                                taskForSubject?.status ?: SurveyState.NOT_STARTED.name
                            )
                        )
                        repository.saveSurveyeeList(hamletSurveyEntity)
                    }
                }
                return true
            } else {
                return false
            }
        } else {
            repository.updateApiStatus(
                SUBPATH_GET_DIDI_LIST,
                status = ApiStatus.FAILED.ordinal,
                apiResponse.message,
                DEFAULT_ERROR_CODE
            )
            return false
        }
        } catch (apiException: ApiException) {
            repository.updateApiStatus(
                SUBPATH_GET_DIDI_LIST,
                status = ApiStatus.FAILED.ordinal,
                apiException.message ?: BLANK_STRING,
                apiException.getStatusCode()
            )
            throw apiException
        } catch (ex: Exception) {
            repository.updateApiStatus(
                SUBPATH_GET_DIDI_LIST,
                status = ApiStatus.FAILED.ordinal,
                ex.message ?: BLANK_STRING,
                DEFAULT_ERROR_CODE
            )
            BaselineLogger.e("FetchUserDetailFromNetworkUseCase", "invoke", ex)
            throw ex
        }
}
}
