package com.patsurvey.nudge.activities.ui.progress

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.UserEntity
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.database.dao.UserDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.dataModel.VillageListModal
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VillageSelectionViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val apiService: ApiService,
    val villageListDao: VillageListDao
): BaseViewModel() {

    private val _villagList = MutableStateFlow(listOf<VillageEntity>())
    val villageList: StateFlow<List<VillageEntity>> get() = _villagList

    val villageSelected = mutableStateOf(-1)

    fun isLoggedIn() = (prefRepo.getAccessToken()?.isNotEmpty() == true)

    init {
        fetchUserDetails()
    }

    fun updateSelectedVillage(){
        prefRepo.saveSelectedVillage(villageSelected.value)
    }

    fun fetchUserDetails() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = apiService.userAndVillageListAPI(prefRepo.getAppLanguageId()?:1)
            withContext(Dispatchers.IO) {
                if (response.status.equals(SUCCESS, true)) {
                    response.data?.let {
                        prefRepo.savePref(PREF_KEY_USER_NAME,it.username)
                        prefRepo.savePref(PREF_KEY_NAME,it.name)
                        prefRepo.savePref(PREF_KEY_EMAIL,it.email)
                        prefRepo.savePref(PREF_KEY_IDENTITY_NUMBER,it.identityNumber)
                        prefRepo.savePref(PREF_KEY_PROFILE_IMAGE,it.profileImage)
                        villageListDao.insertAll(it.villageList)
                        _villagList.emit(it.villageList)
                    }
                }
            }
        }
    }
}