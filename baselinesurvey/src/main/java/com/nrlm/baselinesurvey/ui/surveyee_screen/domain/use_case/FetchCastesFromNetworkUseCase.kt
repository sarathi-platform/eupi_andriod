//package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case
//
//import com.nrlm.baselinesurvey.BLANK_STRING
//import com.nrlm.baselinesurvey.DEFAULT_ERROR_CODE
//import com.nrlm.baselinesurvey.DEFAULT_SUCCESS_CODE
//import com.nrlm.baselinesurvey.SUCCESS
//import com.nrlm.baselinesurvey.model.datamodel.CasteModel
//import com.nrlm.baselinesurvey.network.ApiException
//import com.nrlm.baselinesurvey.network.SUBPATH_GET_CASTE_LIST
//import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
//import com.nrlm.baselinesurvey.utils.BaselineLogger
//import com.nrlm.baselinesurvey.utils.json
//import com.nudge.core.enums.ApiStatus
////            var localLanguageList = repository.fetchLocalLanguageList()
//class FetchCastesFromNetworkUseCase(private val repository: DataLoadingScreenRepository) {
//
//    suspend operator fun invoke(loadAllCastes: Boolean) {
//
//        try {
//            if (!repository.isNeedToCallApi(SUBPATH_GET_CASTE_LIST)) {
//                return
//            }
//
//            val casteList = arrayListOf<CasteModel>()
//            if(localLanguageList.isNotEmpty()){
//                repository.insertApiStatus(SUBPATH_GET_CASTE_LIST)
//
//                localLanguageList.forEach { language ->
//
//                    val casteApiResponse = repository.getCasteListFromNetwork(language.id)
//
//                    if (casteApiResponse.status.equals(SUCCESS, true)) {
//                        if(casteApiResponse.data != null) {
//                            repository.updateApiStatus(
//                                SUBPATH_GET_CASTE_LIST,
//                                status = ApiStatus.SUCCESS.ordinal,
//                                BLANK_STRING,
//                                DEFAULT_SUCCESS_CODE
//                            )
//
//                            casteApiResponse.data?.let { remoteCasteList ->
//                                remoteCasteList.forEach { casteEntity ->
//                                    casteEntity.languageId = language.id
//                                }
//                                casteList.addAll(casteApiResponse.data)
//                            }
//                        }
//                    } else {
//                        repository.updateApiStatus(
//                            SUBPATH_GET_CASTE_LIST,
//                            status = ApiStatus.FAILED.ordinal,
//                            casteApiResponse.message,
//                            DEFAULT_ERROR_CODE
//                        )
//                    }
//                    repository.saveCasteList(casteList.json())
//                }
//            }
//
//        } catch (apiException: ApiException) {
//            repository.updateApiStatus(
//                SUBPATH_GET_CASTE_LIST,
//                status = ApiStatus.FAILED.ordinal,
//                apiException.message ?: BLANK_STRING,
//                apiException.getStatusCode()
//            )
//            throw apiException
//        } catch (ex: Exception) {
//            repository.updateApiStatus(
//                SUBPATH_GET_CASTE_LIST,
//                status = ApiStatus.FAILED.ordinal,
//                ex.message ?: BLANK_STRING,
//                DEFAULT_ERROR_CODE
//            )
//            BaselineLogger.e("FetchUserDetailFromNetworkUseCase", "invoke", ex)
//            throw ex
//        }
//    }
//
//}