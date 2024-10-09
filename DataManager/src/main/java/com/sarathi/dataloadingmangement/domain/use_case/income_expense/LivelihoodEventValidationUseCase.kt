package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import android.text.TextUtils
import com.nudge.core.model.uiModel.LivelihoodModel
import com.nudge.core.utils.ExpressionEvaluator
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.ProductAssetUiModel
import com.sarathi.dataloadingmangement.repository.liveihood.IAssetJournalRepository
import com.sarathi.dataloadingmangement.repository.liveihood.IAssetRepository
import javax.inject.Inject

class LivelihoodEventValidationUseCase @Inject constructor(
    private val assetJournalRepository: IAssetJournalRepository,
    private val assetRepository: IAssetRepository
) {
    suspend fun invoke(
        validationExpression: String?,
        selectedLivelihood: LivelihoodModel,
        subjectId: Int,
        selectedAsset: ProductAssetUiModel?,
        assetCount: String,
        amount: String,
        message: String
    ): Pair<Boolean, String> {
        if (!TextUtils.isEmpty(validationExpression) || !(TextUtils.isEmpty(message))) {
            var map = HashMap<String, String>()
            val expressionArray = validationExpression?.split(" ")
            val msgExpressionArray = message?.split(" ")
            ValidationExpressionEnum.values().forEach {
                if (expressionArray?.contains(it.name) == true || msgExpressionArray?.contains(it.name) == true) {
                    map[it.name] = ""
                }
            }
            if (validationExpression?.contains("{") == true && validationExpression.contains("}")) {
                extractSubstrings(validationExpression).forEach {
                    map[it] = ""
                }
            }

            map.forEach {
                when {
                    it.key.contains("{") && it.key.contains("}") && it.key.contains(
                        "ASSET_COUNT"
                    ) -> {
                        val assetType =
                            it.key.replace("{", "").replace("}", "").split("%").firstOrNull()
                        val assetIds =
                            assetRepository.getAssetsForLivelihood(selectedLivelihood.livelihoodId)
                                .filter { it.type.equals(assetType, true) }.map { it.id }

                        map[it.key] =
                            assetJournalRepository.getTotalAssetCountForParticularAssetType(
                                livelihoodId = selectedLivelihood.livelihoodId,
                                subjectId = subjectId,
                                assetIds = assetIds
                            ).toString()
                    }

                    it.key == ValidationExpressionEnum.TOTAL_ASSET_COUNT.name -> {
                        map[it.key] =
                            assetJournalRepository.getTotalAssetCount(
                                livelihoodId = selectedLivelihood.livelihoodId,
                                subjectId = subjectId
                            ).toString()
                    }

                    it.key == ValidationExpressionEnum.ASSET_TYPE.name -> {
                        map[it.key] = selectedAsset?.id.toString()
                    }

                    it.key == ValidationExpressionEnum.SELECTED_ASSET_COUNT.name -> {
                        map[it.key] = assetCount.toString()
                    }

                    it.key == ValidationExpressionEnum.SELECTED_AMOUNT.name -> {
                        map[it.key] = amount
                    }


                    it.key == ValidationExpressionEnum.TOTAL_SELECTED_ASSET_COUNT.name -> {
                        map[it.key] =
                            assetJournalRepository.getTotalAssetCount(
                                livelihoodId = selectedLivelihood.livelihoodId,
                                subjectId = subjectId,
                                assetId = selectedAsset?.id ?: -1
                            ).toString()
                    }

                }
            }
            var completeExpression = validationExpression
            var validationMessage = message
            map.forEach {
                completeExpression = completeExpression?.replace(it.key, it.value)
                validationMessage = validationMessage.replace(it.key, it.value)

            }
            return Pair(
                ExpressionEvaluator.evaluateExpression(completeExpression ?: BLANK_STRING),
                validationMessage
            )

        }
        return Pair(true, BLANK_STRING)
    }

    enum class ValidationExpressionEnum {

        TOTAL_ASSET_COUNT,
        ASSET_TYPE,
        SELECTED_ASSET_COUNT,
        TOTAL_SELECTED_ASSET_COUNT,
        SELECTED_AMOUNT,

    }

    fun extractSubstrings(input: String): List<String> {
        // Define the regex pattern
        val pattern = "\\{[^%]+%[^}]+\\}".toRegex()

        // Find all matches in the input string
        return pattern.findAll(input).map { it.value }.toList()
    }
}