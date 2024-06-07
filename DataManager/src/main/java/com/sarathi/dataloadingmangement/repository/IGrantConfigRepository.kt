package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.GrantConfigEntity

interface IGrantConfigRepository {
    suspend fun getGrantConfig(activityConfigId: Int): List<GrantConfigEntity>

}