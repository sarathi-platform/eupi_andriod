package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.LIVELIHOOD_TABLE_NAME
import com.sarathi.dataloadingmangement.MISSION_TABLE_NAME
import com.sarathi.dataloadingmangement.model.mat.response.MissionResponse
import com.sarathi.dataloadingmangement.network.response.AssetsResponse


@Entity(tableName = LIVELIHOOD_TABLE_NAME)
data class AssetsEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var subjectId: Int,
    var primaryOptionId: Int,
    var secondaryOptionId: Int,
    var status: Int,
    var isActive: Int = 1
) {
    companion object {
        fun getLivelihoodEntity(
            userId: String,

        ): AssetsEntity {

            return AssetsEntity(
                id = 0,
                userId = userId,
                primaryOptionId = 0,
                subjectId = 0,
                secondaryOptionId = 0,
                status = 0


            )
        }

    }
}