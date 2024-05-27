package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.CONTENT_CONFIG_TABLE_NAME
import com.sarathi.dataloadingmangement.model.mat.response.ContentResponse

@Entity(tableName = CONTENT_CONFIG_TABLE_NAME)
data class ContentConfigEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var matId: Int,
    var key: String,
    var type: String,
    var contentCategory: Int
) {
    companion object {

        fun getContentConfigEntity(
            userId: String,
            matId: Int,
            contents: ContentResponse,
            contentCategory: Int,
        ): ContentConfigEntity {
            return ContentConfigEntity(
                id = 0,
                userId = userId,
                matId = matId,
                key = contents.contentKey,
                type = contents.contentType,
                contentCategory = contentCategory
            )
        }
    }

}
