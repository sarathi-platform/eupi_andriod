package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.ProductDao
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.ProductAssetUiModel
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao, private val coreSharedPrefs: CoreSharedPrefs
) : IProductRepository {
    override suspend fun getProductsForLivelihood(livelihoodId: Int): List<ProductAssetUiModel> {
        return productDao.getProductsForLivelihood(
            livelihoodId = livelihoodId,
            languageCode = coreSharedPrefs.getAppLanguage(),
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }


}