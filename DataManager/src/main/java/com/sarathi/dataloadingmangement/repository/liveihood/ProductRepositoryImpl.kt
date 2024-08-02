package com.sarathi.dataloadingmangement.repository.liveihood

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.livelihood.ProductDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.ProductEntity
import com.sarathi.dataloadingmangement.model.response.Product
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val coreSharedPrefs: CoreSharedPrefs

) : IProductRepository {

    override suspend fun saveProductEntityToDB(product: Product) {
        productDao.insertLivelihood(
            ProductEntity.getLivelihoodEntity(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                product = product
            )
        )
    }
}