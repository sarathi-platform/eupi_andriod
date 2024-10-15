package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.PRODUCT_TABLE_NAME
import com.sarathi.dataloadingmangement.model.response.Product


@Entity(tableName = PRODUCT_TABLE_NAME)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var productId: Int,
    var livelihoodId: Int,
    var userId: String,
    var name: String,
    var status: Int,
    //Todo Add migration for this column
    var type: String = BLANK_STRING,

    ) {
    companion object {
        fun getProductEntity(
            userId: String,
            product: Product,
            livelihoodId: Int
        ): ProductEntity {

            return ProductEntity(
                id = 0,
                productId = product.id ?: 0,
                userId = userId,
                name = product.name ?: BLANK_STRING,
                status = product.status ?: 0,
                livelihoodId = livelihoodId,
                type = product.type ?: BLANK_STRING
            )
        }

    }
}