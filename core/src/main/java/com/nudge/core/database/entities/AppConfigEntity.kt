package com.nudge.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nudge.core.APP_CONFIG_TABLE

@Entity(tableName = APP_CONFIG_TABLE)
data class AppConfigEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo("key")
    val key: String,

    @ColumnInfo("value")
    val value: String,

    @ColumnInfo("status")
    val status: Int,

    @ColumnInfo("createdDate")
    val createdDate: Long,

    @ColumnInfo("modifiedDate")
    val modifiedDate: Long,

    @ColumnInfo("userId")
    val userId: String,


    ) {
    companion object {

        fun getAppConfigEntity(key: String, value: String, userId: String): AppConfigEntity {
            return AppConfigEntity(
                id = 0,
                userId = userId,
                key = key,
                value = value,
                status = 1,//By default it is active,
                createdDate = System.currentTimeMillis(),
                modifiedDate = System.currentTimeMillis()

            )
        }
    }
}


