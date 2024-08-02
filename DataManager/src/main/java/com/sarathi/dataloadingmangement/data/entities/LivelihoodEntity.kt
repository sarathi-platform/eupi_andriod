package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.LIVELIHOOD_TABLE_NAME
import com.sarathi.dataloadingmangement.network.response.AssetsResponse


@Entity(tableName = LIVELIHOOD_TABLE_NAME)
data class LivelihoodEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String,
    var assetsId: Int,
    var status: Int,
    var isActive: Int = 1,

) {
    companion object {
        fun getLivelihoodEntity(
            userId: String,
           assets: AssetsResponse,
        ): LivelihoodEntity {

            return LivelihoodEntity(
                id = 0,
                userId = userId,
                assetsId = assets.id!!,
                status = assets.status,

            )
        }

    }
}