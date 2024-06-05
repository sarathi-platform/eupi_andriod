package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.ATTRIBUTE_VALUE_REFERENCE_ENTITY_TABLE_NAME
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.mat.response.TaskData

@Entity(tableName = ATTRIBUTE_VALUE_REFERENCE_ENTITY_TABLE_NAME)
data class AttributeValueReferenceEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var key: String,
    var value: String,
    var parentReferenceId: Long

) {
    companion object {
        fun getAttributeValueReferenceEntity(
            userId: String?,
            parentReferenceId: Long,
            taskData: TaskData
        ): AttributeValueReferenceEntity {

            return AttributeValueReferenceEntity(
                id = 0, userId = userId, parentReferenceId = parentReferenceId,
                key = taskData.key, value = taskData.value ?: BLANK_STRING

            )

        }

    }
}
