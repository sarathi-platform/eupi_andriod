package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sarathi.dataloadingmangement.ASSETS_TABLE_NAME
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.response.Asset


@Entity(tableName = ASSETS_TABLE_NAME)
data class AssetEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var assetId: Int,
    var livelihoodId: Int,
    var userId: String,
    var name: String,
    var status: Int,
    var type: String = BLANK_STRING,
    var value: Double? = 0.0,
    var image: String?
) {
    companion object {
        fun getAssetEntity(
            userId: String,
            asset: Asset,
            livelihoodId: Int,
            value: Double?,
            image: String?,
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
                type = asset.type ?: BLANK_STRING
            )
        }

    }
}