package com.patsurvey.nudge.activities.ui.digital_forms

import android.content.Context
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
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

    fun generateFormAPdf(context: Context, callBack: (formGenerated: Boolean, formPath: File?) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val villageEntity = prefRepo.getSelectedVillage()
            val success = PdfUtils.getFormAPdf(context = context, villageEntity = villageEntity,
                didiDetailList = didiDetailList.value, prefRepo.getPref(PREF_WEALTH_RANKING_COMPLETION_DATE, "") ?: "")
            withContext(Dispatchers.Main) {
                delay(500)
                val path = if (success) PdfUtils.getPdfPath(context = context, formName = FORM_A_PDF_NAME, villageEntity.name) else null
                callBack(success, path)
            }
        }
    }

    fun generateFormBPdf(context: Context, callBack: (formGenerated: Boolean, formPath: File?) -> Unit)  {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val villageEntity = prefRepo.getSelectedVillage()
            val success = PdfUtils.getFormBPdf(context, villageEntity = prefRepo.getSelectedVillage(),
                didiDetailList = didiDetailList.value.filter { it.patSurveyStatus == PatSurveyStatus.COMPLETED.ordinal },
                prefRepo.getPref(PREF_PAT_COMPLETION_DATE, "") ?: "")
            withContext(Dispatchers.Main) {
                delay(500)
                val path = if (success) PdfUtils.getPdfPath(context = context, formName = FORM_B_PDF_NAME, villageEntity.name) else null
                callBack(success, path)
            }
        }
    }

    fun generateFormCPdf(context: Context, callBack: (formGenerated: Boolean, formPath: File?) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val villageEntity = prefRepo.getSelectedVillage()
            val success = PdfUtils.getFormCPdf(context, villageEntity = prefRepo.getSelectedVillage(),
                didiDetailList = didiDetailList.value.filter { it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal },
                prefRepo.getPref(PREF_VO_ENDORSEMENT_COMPLETION_DATE, "") ?: "")
            withContext(Dispatchers.Main) {
                delay(500)
                val path = if (success) PdfUtils.getPdfPath(context = context, formName = FORM_C_PDF_NAME, villageEntity.name) else null
                callBack(success, path)
            }
        }
    }
}