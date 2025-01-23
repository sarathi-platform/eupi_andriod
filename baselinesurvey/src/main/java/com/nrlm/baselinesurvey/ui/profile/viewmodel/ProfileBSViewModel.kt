package com.nrlm.baselinesurvey.ui.profile.viewmodel

import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.ui.profile.domain.use_case.ProfileBSUseCase
import com.nudge.core.helper.TranslationEnum
import com.nudge.core.toCamelCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileBSViewModel @Inject constructor(
    val profileBSUseCase: ProfileBSUseCase
) : BaseViewModel() {
    val userDetailList = arrayListOf<Pair<String, String>>()
    override fun <T> onEvent(event: T) {
    }

    init {
        setTranslationConfig()
        getAllUserDetails()
    }

    fun getAllUserDetails() {
        userDetailList.add(
            Pair(
                getString(R.string.profile_name),
                profileBSUseCase.getUserNameUseCase.invoke()
            )
        )
        userDetailList.add(
            Pair(
                getString(R.string.profile_phone),
                profileBSUseCase.getUserMobileNumberUseCase.invoke()
            )
        )
        if (profileBSUseCase.getIdentityNumberUseCase.invoke().isNotEmpty()) {
            userDetailList.add(
                Pair(
                    getString(R.string.profile_identity_num),
                    profileBSUseCase.getIdentityNumberUseCase.invoke()
                )
            )
        }

        if (profileBSUseCase.getUserNameUseCase.getBlockName().isNotEmpty()) {
            userDetailList.add(
                Pair(
                    getString(R.string.profile_block_name),
                    profileBSUseCase.getUserNameUseCase.getBlockName().toCamelCase()
                )
            )
        }

        if (profileBSUseCase.getUserNameUseCase.getDistrictName().isNotEmpty()) {
            userDetailList.add(
                Pair(
                    getString(R.string.profile_district_name),
                    profileBSUseCase.getUserNameUseCase.getDistrictName().toCamelCase()
                )
            )
        }

        if (profileBSUseCase.getUserNameUseCase.getStateName().isNotEmpty()) {
            userDetailList.add(
                Pair(
                    getString(R.string.profile_state_name),
                    profileBSUseCase.getUserNameUseCase.getStateName().toCamelCase()
                )
            )
        }

    }

    override fun getScreenName(): TranslationEnum {
        return TranslationEnum.ProfileBSScreen
    }
}