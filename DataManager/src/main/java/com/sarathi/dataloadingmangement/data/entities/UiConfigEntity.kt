package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.model.mat.response.AttributeResponse
import com.sarathi.dataloadingmangement.util.UI_CONFIG_TABLE_NAME

@Entity(tableName = UI_CONFIG_TABLE_NAME)
data class UiConfigEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var key: String,
    var type: String,
    var value: String,
    var componentType: String,
    var language: String,
    var activityId: Int,
    var missionId: Int,
    var userId: String
) {
    companion object {

        fun getUiConfigEntity(
            missionId: Int,
            activityId: Int,
            attributes: AttributeResponse,
            language: String,
            userId: String
        ): UiConfigEntity {
            return UiConfigEntity(
                id = 0,
                key = attributes.key,
                type = attributes.type,
                value = attributes.value,
                componentType = attributes.componentType,
                missionId = missionId,
                activityId = activityId,
                language = language,
                userId = userId
            )

        }
    }
}
