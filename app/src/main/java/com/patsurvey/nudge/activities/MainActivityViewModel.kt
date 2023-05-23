package com.patsurvey.nudge.activities

import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.network.model.ErrorModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val prefRepo: PrefRepo
): BaseViewModel() {
    val isLoggedIn = mutableStateOf(false)
    fun isLoggedIn() = (prefRepo.getAccessToken()?.isNotEmpty() == true)
    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }
}