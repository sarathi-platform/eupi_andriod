package com.sarathi.dataloadingmangement.data.entities


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.TAG_REFERENCE_TABLE_NAME

@Entity(tableName = TAG_REFERENCE_TABLE_NAME)
data class TagReferenceEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var referenceId: Int,
    var referenceType: String,
    var value: Int

) {
    companion object {
        fun getTagReferenceEntity(
            userId: String,
            referenceId: Int,
            referenceType: String,
            tags: List<Int>,
        ): List<TagReferenceEntity> {
            val tagReferenceEntities = ArrayList<TagReferenceEntity>()
            tags.forEach {
                tagReferenceEntities.add(
                    TagReferenceEntity(
                        userId = userId,
                        id = 0,
                        referenceId = referenceId,
                        referenceType = referenceType,
                        value = it
                    )
                )
            }
            return tagReferenceEntities

        }
    }
}
