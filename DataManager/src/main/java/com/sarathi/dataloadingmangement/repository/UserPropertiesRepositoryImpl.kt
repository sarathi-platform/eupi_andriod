package com.sarathi.dataloadingmangement.repository

import android.text.TextUtils
import com.nudge.core.CRP_USER_TYPE
import com.nudge.core.preference.CoreSharedPrefs
import javax.inject.Inject

class UserPropertiesRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs
) : UserPropertiesRepository {

    override fun compareWithPreviousUser(): Boolean {
        return TextUtils.isEmpty(coreSharedPrefs.getPreviousUserMobile()) || coreSharedPrefs.getPreviousUserMobile()
            .equals(coreSharedPrefs.getMobileNo())
    }

    override fun isUserDataLoaded(userType: String): Boolean {
        return if (userType == CRP_USER_TYPE) coreSharedPrefs.isCrpDataLoaded() else coreSharedPrefs.isBpcDataLoaded()

    }

    override fun getStateId(): Int {
        return coreSharedPrefs.getStateId()
    }

}