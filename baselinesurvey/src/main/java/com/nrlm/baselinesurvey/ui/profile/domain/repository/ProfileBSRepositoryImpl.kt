package com.nrlm.baselinesurvey.ui.profile.domain.repository

import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.PREF_KEY_EMAIL
import com.nrlm.baselinesurvey.PREF_KEY_IDENTITY_NUMBER
import com.nrlm.baselinesurvey.PREF_KEY_NAME
import com.nrlm.baselinesurvey.PREF_MOBILE_NUMBER
import com.nrlm.baselinesurvey.data.prefs.PrefBSRepo

class ProfileBSRepositoryImpl(private val prefBSRepo: PrefBSRepo):ProfileBSRepository {
    override fun getUserName(): String {
        return prefBSRepo.getPref(PREF_KEY_NAME, BLANK_STRING)?: BLANK_STRING
    }

    override fun getUserEmail(): String {
        return prefBSRepo.getPref(PREF_KEY_EMAIL, BLANK_STRING)?: BLANK_STRING
    }
    override fun getUserMobileNumber(): String {
        return prefBSRepo.getPref(PREF_MOBILE_NUMBER, BLANK_STRING)?: BLANK_STRING

    }
    override fun getUserIdentityNumber(): String {
        return prefBSRepo.getPref(PREF_KEY_IDENTITY_NUMBER, BLANK_STRING)?: BLANK_STRING
    }

}