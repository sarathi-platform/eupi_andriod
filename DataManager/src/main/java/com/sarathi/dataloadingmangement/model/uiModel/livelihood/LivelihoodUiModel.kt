package com.sarathi.dataloadingmangement.model.uiModel.livelihood

data class LivelihoodUiEntity(
    val id: Int,
    val livelihoodEntity: LivelihoodModel,
    val isSelected: Boolean
) {

    companion object {
        fun getLivelihoodUiEntity(
            livelihoodUiModel: LivelihoodModel,
            isSelected: Boolean
        ): LivelihoodUiEntity {
            return LivelihoodUiEntity(livelihoodUiModel.id, livelihoodUiModel, isSelected)
        }

        fun getLivelihoodUiEntityList(
            livelihoodUiModelList: List<LivelihoodModel>,
            selectedIds: List<Int>
        ): List<LivelihoodUiEntity> {
            val livelihoodUIEntityList = ArrayList<LivelihoodUiEntity>()

            livelihoodUiModelList.forEach { livelihoodDropDownUiModel ->
                livelihoodUIEntityList.add(
                    LivelihoodUiEntity(
                        livelihoodDropDownUiModel.id,
                        livelihoodDropDownUiModel,
                        isSelected = selectedIds.contains(livelihoodDropDownUiModel.id)
                    )
                )
            }

            return livelihoodUIEntityList
        }


    }

}