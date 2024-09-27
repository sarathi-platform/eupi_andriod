package com.sarathi.dataloadingmangement.model.uiModel

import com.nudge.core.BLANK_STRING

data class SmallGroupSubTabUiModel(
    val userId: String,
    val smallGroupId: Int,
    val smallGroupName: String,
    val didiCount: Int
) {

    companion object {
        fun getEmptyModel(): SmallGroupSubTabUiModel {
            return SmallGroupSubTabUiModel(
                userId = BLANK_STRING,
                smallGroupId = 0,
                smallGroupName = BLANK_STRING,
                didiCount = 0
            )
        }
    }

}
