package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.PRODUCT_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.livelihood.ProductEntity
import com.sarathi.dataloadingmangement.enums.LivelihoodLanguageReferenceType
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.ProductAssetUiModel


@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(productEntity: ProductEntity)

    @Query("DELETE FROM $PRODUCT_TABLE_NAME where userId=:userId ")
    fun deleteProductForUser(userId: String)


    @Query(
        "select product_table.productId as id, livelihood_language_reference_table.name, product_table.name as originalName \n" +
                " from product_table inner join livelihood_language_reference_table \n" +
                " on product_table.productId= livelihood_language_reference_table.id \n" +
                " where  livelihood_language_reference_table.languageCode=:languageCode and\n" +
                " livelihood_language_reference_table.referenceType=:referenceType and" +
                " livelihood_language_reference_table.userId=:userId and" +
                " product_table.userId=:userId  and" +
                " product_table.livelihoodId=:livelihoodId group by product_table.productId"
    )
    fun getProductsForLivelihood(
        livelihoodId: Int,
        userId: String,
        referenceType: String = LivelihoodLanguageReferenceType.Product.name,
        languageCode: String,
    ): List<ProductAssetUiModel>

}