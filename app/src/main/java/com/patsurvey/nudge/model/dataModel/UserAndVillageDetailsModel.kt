package com.patsurvey.nudge.model.dataModel

import com.patsurvey.nudge.database.VillageEntity

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
    val typeName: String
)
