package com.sarathi.dataloadingmangement.model.uiModel.livelihood

import com.sarathi.dataloadingmangement.BLANK_STRING

data class SubjectEntityWithLivelihoodMappingUiModel(
    val subjectId: Int,
    val subjectName: String,
    val image: String?,
    val dadaName: String,
    val cohortId: Int,
    val cohortName: String,
    val houseNo: String,
    val villageId: Int,
    val villageName: String,
    val crpImageLocalPath: String = BLANK_STRING,
    val voName: String = BLANK_STRING,
    val primaryLivelihoodId: Int,
    val secondaryLivelihoodId: Int,
)