package com.nudge.syncmanager.domain.repository

import com.nudge.core.BLANK_STRING
import com.nudge.core.CRP_USER_TYPE
import com.nudge.core.preference.CorePrefRepo
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_EMAIL
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_NAME
import com.nudge.core.preference.CoreSharedPrefs.Companion.PREF_KEY_TYPE_NAME
import javax.inject.Inject

class SyncUserDetailsRepositoryImpl @Inject constructor(
    val corePrefRepo: CorePrefRepo,
) : SyncUserDetailsRepository {
    override fun getUserMobileNumber(): String {
        return corePrefRepo.getMobileNo()
    }

    override fun getUserID(): String {
        return corePrefRepo.getUserId()
    }

    override fun getUserEmail(): String {
        return corePrefRepo.getPref(PREF_KEY_EMAIL, BLANK_STRING) ?: BLANK_STRING
    }

    override fun getUserName(): String {
        return corePrefRepo.getPref(PREF_KEY_NAME, BLANK_STRING) ?: BLANK_STRING
    }

    override fun getLoggedInUserType(): String {
        return corePrefRepo.getPref(PREF_KEY_TYPE_NAME, CRP_USER_TYPE) ?: CRP_USER_TYPE
    }
}