package com.patsurvey.nudge.activities.ui.progress.domain.repository.interfaces

import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.model.response.ApiResponseModel

interface FetchCasteListRepository {

    fun getAllCastesForLanguage(languageId: Int): List<CasteEntity>

    fun fetchCasteListFromNetwork(languageId: Int): ApiResponseModel<List<CasteEntity>>

    fun saveCasteListToDb(casteList: List<CasteEntity>)

}