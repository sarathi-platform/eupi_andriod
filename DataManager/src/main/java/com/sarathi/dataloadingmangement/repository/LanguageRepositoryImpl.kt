//package com.sarathi.dataloadingmangement.repository
//
//import com.nudge.core.model.ApiResponseModel
//import com.sarathi.dataloadingmangement.data.dao.LanguageDao
//import com.sarathi.dataloadingmangement.data.entities.LanguageEntity
//import com.sarathi.dataloadingmangement.network.DataLoadingApiService
//import com.sarathi.dataloadingmangement.network.response.ConfigResponseModel
//import javax.inject.Inject
//
//class LanguageRepositoryImpl @Inject constructor(
//    val apiInterface: DataLoadingApiService,
//    val languageDao: LanguageDao,
//) : ILanguageRepository {
//    override suspend fun fetchLanguageDataFromServer(): ApiResponseModel<ConfigResponseModel?> {
//        return apiInterface.fetchLanguageConfigDetailsFromNetwork()
//    }
//
//    override suspend fun saveLanguageData(languages: List<LanguageEntity>) {
//        languageDao.insertAll(languages)
//    }
//}