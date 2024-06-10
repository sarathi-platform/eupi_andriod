package com.sarathi.dataloadingmangement.model.uiModel

import com.sarathi.dataloadingmangement.data.entities.GrantComponentDTO

data class GrantConfigUiModel(
    val grantComponentDTO: GrantComponentDTO?,
    val grantId: Int
)
