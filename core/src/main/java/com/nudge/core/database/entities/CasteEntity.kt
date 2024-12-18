package com.nudge.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.CASTE_TABLE
import com.nudge.core.model.CasteModel

@Entity(tableName = CASTE_TABLE)
data class CasteEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,
    @SerializedName("casteId")
    @Expose
    @ColumnInfo(name = "casteId")
    val casteId: Int? = 0,

    @SerializedName("casteName")
    @Expose
    @ColumnInfo(name = "casteName")
    var casteName: String,

    @SerializedName("languageId")
    @Expose
    @ColumnInfo(name = "languageId")
    var languageId: Int,
    @SerializedName("languageCode")
    @Expose
    @ColumnInfo(name = "languageCode")
    var languageCode: String
) {
    companion object {
        fun getCasteEntity(casteModel: CasteModel): CasteEntity {
            return CasteEntity(
                casteId = casteModel.id,
                id = 0,
                casteName = casteModel.casteName,
                languageId = casteModel.languageId,
                languageCode = casteModel.languageCode
            )

        }
    }
}
