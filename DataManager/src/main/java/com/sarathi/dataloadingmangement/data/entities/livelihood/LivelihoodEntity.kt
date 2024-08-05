package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.LIVELIHOOD_TABLE_NAME
import com.sarathi.dataloadingmangement.model.response.Livelihood


@Entity(tableName = LIVELIHOOD_TABLE_NAME)
data class LivelihoodEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("primaryKey")
    @Expose
    @ColumnInfo(name = "primaryKey")
    var primaryKey: Int = 0,
    var id: Int,
    var userId: String,
    var name: String,
    var status: Int,
    var type: Int? = 0,

    ) {
    companion object {
        fun getLivelihoodEntity(
            userId: String,
            livelihood: Livelihood
        ): LivelihoodEntity {

            return LivelihoodEntity(
                primaryKey = 0,
                id = livelihood.id ?: 0,
                userId = userId,
                name = livelihood.name ?: BLANK_STRING,
                status = livelihood.status ?: 0,
            )
        }

    }
}