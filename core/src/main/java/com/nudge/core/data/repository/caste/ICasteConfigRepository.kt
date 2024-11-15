package com.nudge.core.data.repository.caste

import com.nudge.core.database.entities.CasteEntity
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.CasteModel

interface ICasteConfigRepository {
    suspend fun getCasteConfigFromNetwork(): ApiResponseModel<List<CasteModel>>
    suspend fun getAllCaste(): List<CasteEntity>
    suspend fun getAllCasteForLanguage(): List<CasteEntity>
    suspend fun getCaste(id: Int): CasteEntity
    suspend fun insertCaste(caste: CasteEntity)
    suspend fun insertAll(castes: List<CasteEntity>)
    suspend fun deleteCasteTable()
    suspend fun deleteCasteTableForLanguage()
}