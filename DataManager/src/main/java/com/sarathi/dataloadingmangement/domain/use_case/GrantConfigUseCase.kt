package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.data.entities.GrantConfigEntity
import com.sarathi.dataloadingmangement.repository.GrantConfigRepositoryImpl
import javax.inject.Inject

class GrantConfigUseCase @Inject constructor(private val grantConfigRepositoryImpl: GrantConfigRepositoryImpl) {

    suspend fun getGrantConfig(activityConfigId: Int): List<GrantConfigEntity> =
        grantConfigRepositoryImpl.getGrantConfig(activityConfigId)


}