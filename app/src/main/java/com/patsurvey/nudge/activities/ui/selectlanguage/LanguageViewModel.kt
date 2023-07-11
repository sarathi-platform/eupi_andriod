package com.patsurvey.nudge.activities.ui.selectlanguage

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.LanguageEntity
import com.patsurvey.nudge.database.dao.LanguageListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
  val prefRepo: PrefRepo,
  val languageListDao: LanguageListDao,
  val villageListDao: VillageListDao
) :BaseViewModel(){


    private val _languageList= MutableStateFlow<List<LanguageEntity>?>(emptyList())
    val languageList=_languageList.asStateFlow()
   val list= mutableStateListOf<LanguageEntity>()
    val languagePosition= mutableStateOf(-1)
    init {
        fetchLanguageList()
    }

    private fun fetchLanguageList() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val list=languageListDao.getAllLanguages()
            withContext(Dispatchers.IO){
                _languageList.value=list
            }
        }
    }
    override fun onServerError(error: ErrorModel?) {
        networkErrorMessage.value= error?.message.toString()
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        networkErrorMessage.value= errorModel?.message.toString()
    }

    fun updateSelectedVillage(languageId:Int) {
        val villageId=prefRepo.getSelectedVillage().id
        job = CoroutineScope(Dispatchers.IO +exceptionHandler).launch {
            withContext(Dispatchers.IO){
                val villageEntity = villageListDao.fetchVillageDetailsForLanguage(villageId, languageId)
                prefRepo.saveSelectedVillage(village = villageEntity)
            }
        }

    }
}