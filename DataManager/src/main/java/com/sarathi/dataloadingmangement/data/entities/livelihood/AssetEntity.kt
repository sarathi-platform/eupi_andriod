package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.ASSETS_TABLE_NAME
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.response.Asset


@Entity(tableName = ASSETS_TABLE_NAME)
data class AssetEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("primaryKey")
    @Expose
    @ColumnInfo(name = "primaryKey")
    var primaryKey: Int = 0,
    var id: Int,
    var userId: String,
    var name: String,
    var status: Int,
    var type: Int? = 0,

    ) {
    companion object {
        fun getAssetEntity(
            userId: String,
            asset: Asset,
        ): AssetEntity {

            return AssetEntity(
                primaryKey = 0,
                id = asset.id ?: 0,
                userId = userId,
                name = asset.name ?: BLANK_STRING,
                status = asset.status ?: 0,
            )
        }

    }
}