package com.patsurvey.nudge.model.dataModel

import com.google.gson.annotations.SerializedName
import com.nudge.core.BLANK_STRING
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.utils.AbleBodiedFlag
import com.patsurvey.nudge.utils.SHGFlag

data class ShgFlagEventModel(

    @SerializedName("villageId") val villageId: Int,
    @SerializedName("name") var name: String = BLANK_STRING,
    @SerializedName("guardianName") var guardianName: String = BLANK_STRING,
    @SerializedName("address") var address: String = BLANK_STRING,
    @SerializedName("cohortId") var cohortId: Int = -1,
    @SerializedName("cohortName") var cohortName: String = BLANK_STRING,
    @SerializedName("deviceId") var deviceId: String,
    @SerializedName("cohortDeviceId") var cohortDeviceId: String? = BLANK_STRING,
    @SerializedName("shgFlag") var shgFlag: String
) {

    companion object {

        fun getShgFlagEventModel(
            didiEntity: DidiEntity,
            tolaDeviceId: String
        ): ShgFlagEventModel {
            return ShgFlagEventModel(
                villageId = didiEntity.villageId,
                name = didiEntity.name,
                guardianName = didiEntity.guardianName,
                address = didiEntity.address,
                cohortId = didiEntity.cohortId,
                cohortName = didiEntity.cohortName,
                deviceId = didiEntity.localUniqueId,
                cohortDeviceId = tolaDeviceId,
                shgFlag = SHGFlag.fromInt(didiEntity.shgFlag).name,
            )
        }

    }

}

data class AbleBodiedFlagEventModel(

    @SerializedName("villageId") val villageId: Int,
    @SerializedName("name") var name: String = BLANK_STRING,
    @SerializedName("guardianName") var guardianName: String = BLANK_STRING,
    @SerializedName("address") var address: String = BLANK_STRING,
    @SerializedName("cohortId") var cohortId: Int = -1,
    @SerializedName("cohortName") var cohortName: String = BLANK_STRING,
    @SerializedName("deviceId") var deviceId: String,
    @SerializedName("cohortDeviceId") var cohortDeviceId: String? = BLANK_STRING,
    @SerializedName("ableBodiedFlag") var ableBodiedFlag: String
) {

    companion object {

        fun getAbleBodiedFlagEventModel(
            didiEntity: DidiEntity,
            tolaDeviceId: String
        ): AbleBodiedFlagEventModel {
            return AbleBodiedFlagEventModel(
                villageId = didiEntity.villageId,
                name = didiEntity.name,
                guardianName = didiEntity.guardianName,
                address = didiEntity.address,
                cohortId = didiEntity.cohortId,
                cohortName = didiEntity.cohortName,
                deviceId = didiEntity.localUniqueId,
                cohortDeviceId = tolaDeviceId,
                ableBodiedFlag = AbleBodiedFlag.fromInt(didiEntity.ableBodiedFlag).name,
            )
        }

    }

}
