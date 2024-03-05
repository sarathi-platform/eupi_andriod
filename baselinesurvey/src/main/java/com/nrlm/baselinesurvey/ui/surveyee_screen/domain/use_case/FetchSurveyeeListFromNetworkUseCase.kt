package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.NO_TOLA_TITLE
import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.ui.language.domain.repository.LanguageScreenRepository
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.toCamelCase

class FetchSurveyeeListFromNetworkUseCase(
    private val repository: DataLoadingScreenRepository
) {

    suspend operator fun invoke(): Boolean {
        val userId = repository.getUserId()
        val apiResponse = repository.fetchSurveyeeListFromNetwork(userId)
        val localSurveyeeEntityList = repository.fetchSurveyeeListFromLocalDb()
        if (apiResponse?.status?.equals(SUCCESS, false) == true) {
            if (apiResponse?.data?.didiList != null) {
//                repository.deleteSurveyeeList()
                apiResponse?.data?.didiList.forEach {
                    if (!localSurveyeeEntityList.map { surveyeeEntity -> surveyeeEntity.didiId }.contains(it.didiId)) { //TODO Modify this if to keep backend changes as well
                        val surveyeeEntity = SurveyeeEntity(
                            id = 0,
                            userId = it.userId,
                            didiId = it.didiId,
                            didiName = it.didiName ?: BLANK_STRING,
                            dadaName = it.dadaName ?: BLANK_STRING,
                            casteId = it.casteId ?: -1,
                            cohortId = it.cohortId ?: -1,
                            cohortName = it.cohortName ?: BLANK_STRING,
                            houseNo = it.houseNo ?: BLANK_STRING,
                            villageId = it.villageId ?: -1,
                            villageName = it.villageName ?: BLANK_STRING,
                            ableBodied = it.ableBodied ?: BLANK_STRING
                        )
                        repository.saveSurveyeeList(surveyeeEntity)
                    }

                }

                apiResponse.data?.didiList?.distinctBy { it.cohortName }?.forEach {
                    if (!localSurveyeeEntityList.map { surveyeeEntity -> surveyeeEntity.didiId }.contains(it.cohortId)) {
                        val hamletSurveyEntity = SurveyeeEntity(
                            id = 0,
                            userId = it.userId,
                            didiId = it.cohortId ?: -1,
                            didiName = if (it.cohortName?.equals(NO_TOLA_TITLE, true) == true) it.villageName?.toCamelCase() ?: BLANK_STRING else it.cohortName ?: BLANK_STRING,
                            dadaName = BLANK_STRING,
                            casteId = -1,
                            cohortId = it.cohortId ?: -1,
                            cohortName = it.villageName ?: BLANK_STRING,
                            houseNo = BLANK_STRING,
                            villageId = it.villageId ?: -1,
                            villageName = it.villageName ?: BLANK_STRING,
                            ableBodied = BLANK_STRING
                        )
                        repository.saveSurveyeeList(hamletSurveyEntity)
                    }
                }
                return true
            } else {
                return false
            }
        } else {
            return false
        }
    }
}
