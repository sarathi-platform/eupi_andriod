package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.utils.TRAINING_VIDEO_TABLE

@Entity(tableName = TRAINING_VIDEO_TABLE)
data class TrainingVideoEntity(
    @PrimaryKey
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    val id: Int,

    @SerializedName("title")
    @Expose
    @ColumnInfo(name = "title")
    val title: String,

    @SerializedName("description")
    @Expose
    @ColumnInfo(name = "description")
    val description: String,

    @SerializedName("url")
    @Expose
    @ColumnInfo(name = "url")
    val url: String,

    @SerializedName("thumbUrl")
    @Expose
    @ColumnInfo(name = "thumbUrl")
    val thumbUrl: String,

    @SerializedName("isDownload")
    @Expose
    @ColumnInfo(name = "isDownload")
    var isDownload: Int
) {

}
