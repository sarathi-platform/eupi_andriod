package com.sarathi.smallgroupmodule.ui.smallGroupAttendanceHistory.presentation.event

import com.nudge.core.model.response.ShgMember
import com.nudge.core.model.response.VillageDetailsFromLokOs

sealed class DidiVerificationEvent {

    data class OnShgSelected(val selectedShg: VillageDetailsFromLokOs, val onSuccess: () -> Unit) :
        DidiVerificationEvent()

    data class OnShgMemberSelected(val selectedShgMember: ShgMember, val onSuccess: () -> Unit) :
        DidiVerificationEvent()

    data class SaveShgVerificationStatus(val onDataSave: () -> Unit) : DidiVerificationEvent()
}