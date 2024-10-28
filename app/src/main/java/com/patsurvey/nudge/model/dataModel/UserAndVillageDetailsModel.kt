package com.patsurvey.nudge.model.dataModel

import com.patsurvey.nudge.database.VillageEntity
import com.sarathi.dataloadingmangement.network.response.FederationDetailModel

data class UserAndVillageDetailsModel(
    val success: Boolean,
    val villageList: List<VillageEntity>,
    val stateId: Int,
) {
    companion object {
        fun getFailedResponseModel() = UserAndVillageDetailsModel(false, emptyList(), -1)
    }
}

data class UserDetailsModel(
    val username: String,
    val name: String,
    val email: String,
    val identityNumber: String,
    val profileImage: String,
    val roleName: String,
    val typeName: String,
    val blockId: Int,
    val blockName: String,
    val districtId: Int,
    val districtName: String,
    val stateId: Int,
    val stateName: String,
)
