package com.sarathi.dataloadingmangement.domain.use_case

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sarathi.dataloadingmangement.data.entities.GrantComponentDTO
import com.sarathi.dataloadingmangement.data.entities.GrantConfigEntity
import com.sarathi.dataloadingmangement.model.uiModel.GrantConfigUiModel
import com.sarathi.dataloadingmangement.repository.GrantConfigRepositoryImpl
import javax.inject.Inject

class GrantConfigUseCase @Inject constructor(private val grantConfigRepositoryImpl: GrantConfigRepositoryImpl) {

    suspend fun getGrantConfig(activityConfigId: Int): List<GrantConfigEntity> =
        grantConfigRepositoryImpl.getGrantConfig(activityConfigId)

    suspend fun getGrantComponentDTO(surveyId: Int, activityConfigId: Int): GrantConfigUiModel {
        val grantConfigEntity = grantConfigRepositoryImpl.getGrantComponentDTO(
            surveyId = surveyId,
            activityConfigId = activityConfigId
        )
        return GrantConfigUiModel(
            grantId = grantConfigEntity.grantId,
            grantType = grantConfigEntity.grantType,
            grantComponentDTO = getGrantComponentValues(grantConfigEntity.grantComponent)
        )
    }

    private fun getGrantComponentValues(grantString: String): GrantComponentDTO? {
        val gson = Gson()
        val type = object :
            TypeToken<GrantComponentDTO>() {}.type
        return gson.fromJson<GrantComponentDTO>(grantString, type)
    }


}