package com.sarathi.dataloadingmangement.model.uiModel.livelihood

import com.nudge.core.value
import com.sarathi.dataloadingmangement.BLANK_STRING

data class DataTabScreenUiModel(
    val subjectId: Int,
    val subjectName: String,
    val imageFileName: String?,
    val dadaName: String,
    val cohortId: Int,
    val cohortName: String,
    val houseNo: String,
    val villageId: Int,
    val villageName: String,
    val crpImageLocalPath: String = BLANK_STRING,
    val voName: String = BLANK_STRING,
    val livelihoodIds: List<Int>,
    val lastUpdated: Long,

    ) {
    companion object {
        fun getDataTabUiEntityList(
            subjectEntityWithLivelihoodMappingUiModelList: List<SubjectEntityWithLivelihoodMappingUiModel>,
            lastEventDateMapForSubject: Map<Int, Long> = hashMapOf()

        ): List<DataTabScreenUiModel> {
            val dataTabList = ArrayList<DataTabScreenUiModel>()
            subjectEntityWithLivelihoodMappingUiModelList.groupBy { it.subjectId }.entries.forEach { it ->
                val subjectEntity = it.value.first()

                dataTabList.add(
                    DataTabScreenUiModel(
                        subjectId = it.key,
                        subjectName = subjectEntity.subjectName,
                        dadaName = subjectEntity.dadaName,
                        cohortId = subjectEntity.cohortId,
                        cohortName = subjectEntity.cohortName,
                        houseNo = subjectEntity.houseNo,
                        villageId = subjectEntity.villageId,
                        villageName = subjectEntity.villageName,
                        crpImageLocalPath = subjectEntity.crpImageLocalPath,
                        voName = subjectEntity.voName,
                        livelihoodIds = it.value.map { it.livelihoodId },
                        lastUpdated = lastEventDateMapForSubject.get(subjectEntity.subjectId)
                            .value(),
                        imageFileName = subjectEntity.image
                    )
                )

            }
            return dataTabList

        }
    }
}