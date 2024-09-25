package com.sarathi.dataloadingmangement.domain.use_case.income_expense

import android.text.TextUtils
import com.nudge.core.utils.ExpressionEvaluator
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.repository.liveihood.IAssetJournalRepository
import javax.inject.Inject

class LivelihoodEventValidationUseCase @Inject constructor(
    private val assetJournalRepository: IAssetJournalRepository,
) {
    suspend fun getAssetCount(
        validationExpression: String?,
        livelihoodId: Int,
        subjectId: Int,
        selectedAssetType: Int
    ): Int {
        if (!TextUtils.isEmpty(validationExpression)) {
            val map = HashMap<String, String>()
            ValidationExpressionEnum.values().forEach {
                if (validationExpression?.contains(it.name) == true) {
                    map[it.name] = ""
                }
            }

            map.forEach {
                when (it.key) {
                    ValidationExpressionEnum.Total_Asset_Count.name ->
                        return assetJournalRepository.getTotalAssetCount(
                            livelihoodId = livelihoodId,
                            subjectId = subjectId
                        )

                    ValidationExpressionEnum.ASSET_TYPE.name ->
                        return selectedAssetType

                    ValidationExpressionEnum.ASSET_TYPE_COUNT.name ->
                        return assetJournalRepository.getTotalAssetCount(
                            livelihoodId = livelihoodId,
                            subjectId = subjectId,
                            assetId = selectedAssetType
                        )
                }
            }
        }
        return 0
    }
    suspend fun invoke(
        validationExpression: String?,
        livelihoodId: Int,
        subjectId: Int,
        selectedAssetType: Int
    ): Boolean {
        if (!TextUtils.isEmpty(validationExpression)) {
            var map = HashMap<String, String>()
            ValidationExpressionEnum.values().forEach {
                if (validationExpression?.contains(it.name) == true) {
                    map[it.name] = ""
                }
            }

            map.forEach {
                when (it.key) {
                    ValidationExpressionEnum.Total_Asset_Count.name -> map[it.key] =
                        assetJournalRepository.getTotalAssetCount(
                            livelihoodId = livelihoodId,
                            subjectId = subjectId
                        ).toString()

                    ValidationExpressionEnum.ASSET_TYPE.name -> map[it.key] =
                        selectedAssetType.toString()

                    ValidationExpressionEnum.ASSET_TYPE_COUNT.name -> map[it.key] =
                        assetJournalRepository.getTotalAssetCount(
                            livelihoodId = livelihoodId,
                            subjectId = subjectId,
                            assetId = selectedAssetType
                        ).toString()

                }
            }
            var completeExpression = validationExpression
            map.forEach {
                completeExpression = completeExpression?.replace(it.key, it.value)

            }
            return ExpressionEvaluator.evaluateExpression(completeExpression ?: BLANK_STRING)

        }
        return true
    }

    enum class ValidationExpressionEnum {

        Total_Asset_Count,
        ASSET_TYPE,
        ASSET_TYPE_COUNT

    }

}