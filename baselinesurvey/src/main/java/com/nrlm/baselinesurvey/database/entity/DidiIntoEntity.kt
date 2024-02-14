package com.nrlm.baselinesurvey.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nrlm.baselinesurvey.DIDI_INFO_TABLE_NAME

@Entity(tableName = DIDI_INFO_TABLE_NAME)
data class DidiIntoEntity(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("didiId")
    @Expose
    @ColumnInfo(name = "didiId")
    //  var id: Int,
    var didiId: Int?,
    var adharNumber: String?,
    var phoneNumber: String?,
    var isVoterCard: Int? = -1,
    var isAdharCard: Int? = -1,
) {
    companion object {
        fun getEmptyDidiIntoEntity() = DidiIntoEntity(
            // id = 0,
            didiId = 0,
            adharNumber = "",
            phoneNumber = "",
            isAdharCard = -1,
            isVoterCard = -1
        )
    }
}