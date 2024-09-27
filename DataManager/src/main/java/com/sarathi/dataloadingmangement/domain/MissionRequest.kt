package com.sarathi.dataloadingmangement.domain

import com.google.gson.annotations.SerializedName

data class MissionRequest(@SerializedName("stateId") val stateId: Int)