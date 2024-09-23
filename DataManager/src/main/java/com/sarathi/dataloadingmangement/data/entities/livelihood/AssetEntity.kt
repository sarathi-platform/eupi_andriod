package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sarathi.dataloadingmangement.ASSETS_TABLE_NAME
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.converters.ValidationConverter
import com.sarathi.dataloadingmangement.model.response.Asset
import com.sarathi.dataloadingmangement.model.response.Validation


@Entity(tableName = ASSETS_TABLE_NAME)
data class AssetEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var assetId: Int,
    var livelihoodId: Int,
    var userId: String,
    var name: String,
    var status: Int,
    var type: Int? = 0,
    var value: Double? = 0.0,
    var image: String?,
    //Todo add migration here
    @TypeConverters(ValidationConverter::class)
    var validations: Validation?
) {
    companion object {
        fun getAssetEntity(
            userId: String,
            asset: Asset,
            livelihoodId: Int,
            value: Double?,
            image: String?,
            validation: Validation?
        ): AssetEntity {
            return AssetEntity(
                id = 0,
                assetId = asset.id ?: 0,
                userId = userId,
                name = asset.name ?: BLANK_STRING,
                status = asset.status ?: 0,
                livelihoodId = livelihoodId,
                value = value,
                image = image,
                validations = validation
            )
        }

    }
}