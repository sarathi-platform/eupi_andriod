package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.FormUiConfigEntity


interface IFormConfigRepository {

    suspend fun getFormUiConfig(): List<FormUiConfigEntity>

}