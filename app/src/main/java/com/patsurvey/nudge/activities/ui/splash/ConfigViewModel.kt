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
    var job: Job? = null

    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    init {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = apiInterface.configDetails()
            withContext(Dispatchers.IO) {
                if (response.status.equals(SUCCESS,true)) {
                    response.data?.let {
                        it.languageList.forEachIndexed {index, language->
                            var code="en"
                            var isSelected=true
                                 if(language.equals("Hindi",true)){
                                  code="hi"
                                     isSelected=false
                                 }
                            if(language.equals("Bengali",true)){
                                code="bn-rIN"
                                isSelected=false
                            }
                                languageListDao.insertLanguage(LanguageEntity(index,language,code,isSelected))
                             }
                    }
                } else {
                    onError("Error : ${response.message} ")
                }
            }
        }

    }

    private fun onError(message: String) {
//        usersLoadError.value = message
//        loading.value = false
    }
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}