package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.patsurvey.nudge.utils.USER_TABLE_NAME

@Entity(tableName = USER_TABLE_NAME)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,

    @SerializedName("name")
    @Expose
    @ColumnInfo(name = "name")
    var name: String,

    @SerializedName("username")
    @Expose
    @ColumnInfo(name = "username")
    var username: String,

    @SerializedName("email")
    @Expose
    @ColumnInfo(name = "email")
    var email: String,

    @SerializedName("identityNumber")
    @Expose
    @ColumnInfo(name = "identityNumber")
    var identityNumber: String,

    @SerializedName("profileImage")
    @Expose
    @ColumnInfo(name = "profileImage")
    var profileImage: String

)
