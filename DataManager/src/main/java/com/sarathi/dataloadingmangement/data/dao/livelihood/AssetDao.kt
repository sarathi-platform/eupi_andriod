package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.livelihood.AssetEntity
import com.sarathi.dataloadingmangement.enums.LivelihoodLanguageReferenceType
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.ProductAssetUiModel


@Dao
interface AssetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAsset(assetEntity: AssetEntity)


    @Query(
        "select assets_table.assetId as id, livelihood_language_reference_table.name, assets_table.name as originalName \n" +
                " from assets_table inner join livelihood_language_reference_table \n" +
                " on assets_table.assetId= livelihood_language_reference_table.referenceId \n" +
                " where  livelihood_language_reference_table.languageCode=:languageCode and\n" +
                " livelihood_language_reference_table.referenceType=:referenceType and" +
                " livelihood_language_reference_table.userId=:userId and" +
                " assets_table.userId=:userId  and" +
                " assets_table.livelihoodId=:livelihoodId group by assets_table.assetId "
    )
    fun getAssetForLivelihood(
        livelihoodId: Int,
        userId: String,
        referenceType: String = LivelihoodLanguageReferenceType.Asset.name,
        languageCode: String,
    ): List<ProductAssetUiModel>

    @Query("SELECT * from assets_table where livelihoodId in (:livelihoodIds) and userId = :userId")
    fun getAllAssetsForLivelihoods(livelihoodIds: List<Int>, userId: String): List<AssetEntity>

    @Query("DELETE from assets_table where userId = :userId")
    fun deleteAssetsForUser(userId: String)


}