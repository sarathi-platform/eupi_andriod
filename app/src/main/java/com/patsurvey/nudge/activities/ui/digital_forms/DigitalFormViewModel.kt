package com.patsurvey.nudge.activities.ui.digital_forms

import android.content.Context
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.PREF_PAT_COMPLETION_DATE
import com.patsurvey.nudge.utils.PREF_WEALTH_RANKING_COMPLETION_DATE
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.PdfUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DigitalFormViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val didiDao: DidiDao,
):BaseViewModel()  {
    private val _didiDetailList = MutableStateFlow(listOf<DidiEntity>())
    val didiDetailList: StateFlow<List<DidiEntity>> get() = _didiDetailList
    var villageId: Int = -1
    init {
        villageId = prefRepo.getSelectedVillage().id
        job= CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
                _didiDetailList.emit(didiDao.getAllPoorDidisForVillage(villageId))
            }
        }
    }
    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

    fun generateFormAPDF(context: Context, callBack: () -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            PdfUtils.getFormAPdf(context = context, villageEntity = prefRepo.getSelectedVillage(), didiDetailList = didiDetailList.value, prefRepo.getPref(PREF_WEALTH_RANKING_COMPLETION_DATE, "") ?: "")
            withContext(Dispatchers.Main) {
                callBack()
            }
        }
    }

    fun generateFormBPDF(context: Context, callBack: () -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            PdfUtils.getFormBPdf(context, villageEntity = prefRepo.getSelectedVillage(), didiDetailList = didiDetailList.value.filter { it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal }, prefRepo.getPref(PREF_PAT_COMPLETION_DATE, "") ?: "")
            withContext(Dispatchers.Main) {
                callBack()
            }
        }
    }
}