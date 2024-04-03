package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.model.datamodel.CasteModel
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.json

class FetchCastesFromNetworkUseCase(private val repository: DataLoadingScreenRepository) {

    suspend operator fun invoke() {

        try {
            val localLanguageList = repository.fetchLocalLanguageList()
            val casteList = arrayListOf<CasteModel>()
            if(localLanguageList.isNotEmpty()){
                localLanguageList.forEach { language ->

                val casteApiResponse =  repository.getCasteListFromNetwork(language.id)
                     if (casteApiResponse.status.equals(SUCCESS, true)) {
                        if(casteApiResponse.data != null) {
                            casteApiResponse.data?.let { remoteCasteList ->
                                remoteCasteList.forEach { casteEntity ->
                                    casteEntity.languageId = language.id
                                }
                                casteList.addAll(casteApiResponse.data)
                            }
                        }
                    }
                    repository.saveCasteList(casteList.json())
                }
            }

        } catch (ex: Exception) {
            BaselineLogger.e("FetchCastesFromNetworkUseCase", "invoke", ex)
        }
    }

}