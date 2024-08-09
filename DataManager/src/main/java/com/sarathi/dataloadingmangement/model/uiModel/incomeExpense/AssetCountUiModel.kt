package com.sarathi.dataloadingmangement.model.uiModel.incomeExpense

import com.nudge.core.value
import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetEntity

data class AssetCountUiModel(
    val subjectId: Int,
    val livelihoodId: Int,
    val assetId: Int,
    val totalAssetCountForFlow: Int,
) {

    companion object {

        fun getAssetCountUiModel(
            subjectId: Int,
            livelihoodId: Int,
            assetId: Int,
            totalAssetCountForFlow: Int
        ): AssetCountUiModel {
            return AssetCountUiModel(subjectId, livelihoodId, assetId, totalAssetCountForFlow)
        }

    }

}


data class AssetsCountWithValueUiModel(
    val assetId: Int,
    val assetCount: Int,
    val totalAssetValue: Double
) {

    companion object {
        fun getAssetsCountWithValueUiModel(
            assetCountUiModel: AssetCountUiModel,
            assetValue: Double?
        ): AssetsCountWithValueUiModel {

            val totalAssetValue = assetCountUiModel.totalAssetCountForFlow * assetValue.value()

            return AssetsCountWithValueUiModel(
                assetCountUiModel.assetId,
                assetCountUiModel.totalAssetCountForFlow,
                totalAssetValue
            )

        }

        fun getAssetsCountWithValueUiModelList(
            assetsList: List<AssetEntity>,
            assetCountUiModelList: List<AssetCountUiModel>
        ): List<AssetsCountWithValueUiModel> {

            val assetsCountWithValueUiModelList = ArrayList<AssetsCountWithValueUiModel>()

            val assetCountUiModelMap = assetCountUiModelList.associateBy { it.assetId }
            assetsList.forEach {
                assetCountUiModelMap[it.assetId]?.let { assetCountUiModel ->
                    assetsCountWithValueUiModelList.add(
                        getAssetsCountWithValueUiModel(
                            assetCountUiModel,
                            it.value
                        )
                    )
                }
            }

            return assetsCountWithValueUiModelList
        }
    }

}

fun List<AssetsCountWithValueUiModel>.find(assetId: Int): AssetsCountWithValueUiModel? {

    if (assetId == -1)
        return null

    if (this.isEmpty())
        return null

    val index = this.map { it.assetId }.indexOf(assetId)

    if (index == -1)
        return null

    return this[index]
}
