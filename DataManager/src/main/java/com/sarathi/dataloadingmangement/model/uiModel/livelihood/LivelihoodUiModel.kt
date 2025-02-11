package com.sarathi.dataloadingmangement.model.uiModel.livelihood

import com.nudge.core.model.uiModel.LivelihoodModel

data class LivelihoodUiEntity(
    val id: Int,
    val livelihoodEntity: LivelihoodModel,
    var isSelected: Boolean,
    var isLivelihoodTypeDropdown: Boolean = false
) {

    companion object {
        fun getLivelihoodUiEntity(
            livelihoodUiModel: LivelihoodModel,
            isSelected: Boolean
        ): LivelihoodUiEntity {
            return LivelihoodUiEntity(
                livelihoodUiModel.programLivelihoodId,
                livelihoodUiModel,
                isSelected
            )
        }

        fun getLivelihoodUiEntityList(
            livelihoodUiModelList: List<LivelihoodModel>,
            selectedIds: List<Int>
        ): List<LivelihoodUiEntity> {
            val livelihoodUIEntityList = ArrayList<LivelihoodUiEntity>()
            livelihoodUiModelList.forEach { livelihoodDropDownUiModel ->
                livelihoodUIEntityList.add(
                    LivelihoodUiEntity(
                        livelihoodDropDownUiModel.programLivelihoodId,
                        livelihoodDropDownUiModel,
                        isSelected = selectedIds.contains(livelihoodDropDownUiModel.programLivelihoodId)
                    )
                )
            }

            return livelihoodUIEntityList.distinct()
        }

        fun getLivelihoodUiTypeEntityList(
            livelihoodUiModelList: List<LivelihoodModel>,
            selectedType: List<String>
        ): List<LivelihoodUiEntity> {
            val livelihoodUIEntityList = ArrayList<LivelihoodUiEntity>()
            livelihoodUiModelList.forEach { livelihoodDropDownUiModel ->
                livelihoodUIEntityList.add(
                    LivelihoodUiEntity(
                        livelihoodDropDownUiModel.programLivelihoodId,
                        livelihoodDropDownUiModel,
                        isSelected = selectedType.contains(livelihoodDropDownUiModel.type),
                        isLivelihoodTypeDropdown = true
                    )
                )
            }

            return livelihoodUIEntityList.distinct()
        }

    }

}