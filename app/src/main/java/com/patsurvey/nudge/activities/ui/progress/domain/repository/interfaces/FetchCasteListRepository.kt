package com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces

import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.model.response.ApiResponseModel

interface FetchCasteListRepository {

    fun getAllCastesForLanguage(languageId: Int): List<CasteEntity>

    suspend fun fetchCasteListFromNetwork(languageId: Int): ApiResponseModel<List<CasteEntity>>?

    suspend fun saveCasteListToDb(casteList: List<CasteEntity>)

    suspend fun deleteCasteForLanguage(languageId: Int)



}