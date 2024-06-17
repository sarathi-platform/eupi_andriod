package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.FORM_UI_CONFIG_TABLE_NAME
import com.sarathi.dataloadingmangement.model.mat.response.FormConfigResponse

@Entity(tableName = FORM_UI_CONFIG_TABLE_NAME)
data class FormUiConfigEntity(
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
    var userId: String
) {
    companion object {

        fun getFormUiConfigEntity(
            attributes: FormConfigResponse?,
            userId: String
        ): FormUiConfigEntity {
            return FormUiConfigEntity(
                id = 0,
                key = attributes?.key ?: BLANK_STRING,
                type = attributes?.type ?: BLANK_STRING,
                value = attributes?.value ?: BLANK_STRING,
                componentType = attributes?.componentType ?: BLANK_STRING,
                language = attributes?.languageId ?: "en",
                userId = userId
            )

        }
    }
}
