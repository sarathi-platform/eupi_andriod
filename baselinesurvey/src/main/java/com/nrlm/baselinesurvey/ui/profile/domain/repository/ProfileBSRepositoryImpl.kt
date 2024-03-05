package com.nrlm.baselinesurvey.ui.profile.domain.repository

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_KEY_EMAIL
import com.nrlm.baselinesurvey.PREF_KEY_IDENTITY_NUMBER
import com.nrlm.baselinesurvey.PREF_KEY_NAME
import com.nrlm.baselinesurvey.PREF_KEY_USER_NAME
import com.nrlm.baselinesurvey.PREF_MOBILE_NUMBER
import com.nrlm.baselinesurvey.data.prefs.PrefRepo

class ProfileBSRepositoryImpl(private val prefRepo: PrefRepo):ProfileBSRepository {
    override fun getUserName(): String {
        return prefRepo.getPref(PREF_KEY_NAME, BLANK_STRING)?: BLANK_STRING
    }

    override fun getUserEmail(): String {
        return prefRepo.getPref(PREF_KEY_EMAIL, BLANK_STRING)?: BLANK_STRING
    }

    override fun getUserMobileNumber(): String {
        return prefRepo.getPref(PREF_KEY_IDENTITY_NUMBER, BLANK_STRING)?: BLANK_STRING

    }

    override fun getUserIdentityNumber(): String {
        return prefRepo.getPref(PREF_MOBILE_NUMBER, BLANK_STRING)?: BLANK_STRING
    }
}