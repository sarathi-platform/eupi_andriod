package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.SUBJECT_ATTRIBUTE_TABLE_NAME

@Entity(tableName = SUBJECT_ATTRIBUTE_TABLE_NAME)
data class SubjectAttributeEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var subjectId: Int,
    var subjectType: String,
    var attribute: String,
    var date: String,
    var tagId: Int,
    var tagType: String,
    var missionId: Int,
    var taskId: Int,
    var activityId: Int
) {
    companion object {

        fun getSubjectAttributeEntity(
            userId: String?,
            subjectId: Int,
            subjectType: String,
            attribute: String = BLANK_STRING,
            missionId: Int,
            activityId: Int,
            date: String = System.currentTimeMillis().toString(),
            taskId: Int,
            tagId: Int = 0,
            tagType: String = BLANK_STRING
        ): SubjectAttributeEntity {

            return SubjectAttributeEntity(
                id = 0,
                userId = userId,
                subjectId = subjectId,
                subjectType = subjectType,
                attribute = attribute,
                activityId = activityId,
                missionId = missionId,
                taskId = taskId,
                tagId = tagId,
                tagType = tagType,
                date = date
            )
        }
    }
}

