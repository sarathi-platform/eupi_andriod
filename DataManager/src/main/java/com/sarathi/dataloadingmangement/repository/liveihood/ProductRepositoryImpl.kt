package com.sarathi.dataloadingmangement.repository.liveihood

import com.sarathi.dataloadingmangement.data.dao.livelihood.ProductDao
import com.sarathi.dataloadingmangement.data.entities.livelihood.ProductEntity
import com.sarathi.dataloadingmangement.model.response.Product
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao

) : IProductRepository {

    override suspend fun saveProductEntityToDB(product: Product) {
        productDao.insertLivelihood(
            ProductEntity.getLivelihoodEntity(
                userId = "",
                product = product
            )
        )
    }
}