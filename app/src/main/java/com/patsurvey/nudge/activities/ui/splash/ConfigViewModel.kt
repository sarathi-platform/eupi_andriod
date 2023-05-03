package com.patsurvey.nudge.activities.ui.splash

import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.SUCCESS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel @Inject constructor(
    prefRepo: PrefRepo,
    apiInterface: ApiService,
    languageListDao: LanguageListDao
):BaseViewModel()  {

    init {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = apiInterface.configDetails()
            withContext(Dispatchers.IO) {
                if (response.status.equals(SUCCESS,true)) {
                    response.data?.let {
                        languageListDao.insertAll(it.languageList)
                    }
                } else {
                    onError("Error : ${response.message} ")
                }
            }
        }
    }
}