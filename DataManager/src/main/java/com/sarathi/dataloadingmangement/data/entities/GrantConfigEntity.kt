package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.GRANT_CONFIG_TABLE_NAME
import com.sarathi.dataloadingmangement.model.mat.response.GrantConfigResponse


@Entity(tableName = GRANT_CONFIG_TABLE_NAME)
data class GrantConfigEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var surveyId: Int,
    var grantId: Int,
    var grantName: String,
    var grantComponent: String,
    var grantMode: String,
    var grantNature: String,
    var activityConfigId: Long,

    ) {
    companion object {

        fun getGrantConfigEntity(
            userId: String,
            activityConfigId: Long,
            surveyId: Int,
            grantConfigResponse: GrantConfigResponse
        ): GrantConfigEntity {
            return GrantConfigEntity(
                id = 0,
                userId = userId,
                activityConfigId = activityConfigId,
                grantId = grantConfigResponse.grantId,
                grantName = grantConfigResponse.grantName,
                grantMode = grantConfigResponse.grantMode,
                grantComponent = grantConfigResponse.grantComponent,
                grantNature = grantConfigResponse.grantNature,
                surveyId = surveyId
            )
        }
    }
}
