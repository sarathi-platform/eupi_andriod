package com.patsurvey.nudge.activities.ui.digital_forms

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.PoorDidiEntity
import com.patsurvey.nudge.database.dao.CasteListDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.database.dao.PoorDidiListDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.FORM_A_PDF_NAME
import com.patsurvey.nudge.utils.FORM_B_PDF_NAME
import com.patsurvey.nudge.utils.FORM_C_PDF_NAME
import com.patsurvey.nudge.utils.PREF_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_VO_ENDORSEMENT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_WEALTH_RANKING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.PdfUtils
import com.patsurvey.nudge.utils.WealthRank
import com.patsurvey.nudge.utils.changeMilliDateToDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DigitalFormViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val didiDao: DidiDao,
    val poorDidiListDao: PoorDidiListDao,
    val casteListDao: CasteListDao
):BaseViewModel()  {
    private val _didiDetailList = MutableStateFlow(listOf<DidiEntity>())
    private val _didiDetailListForBpc = MutableStateFlow(listOf<PoorDidiEntity>())
    private val _casteList = MutableStateFlow(listOf<CasteEntity>())
    val didiDetailList: StateFlow<List<DidiEntity>> get() = _didiDetailList
    val didiDetailListForBpc: StateFlow<List<PoorDidiEntity>> get() = _didiDetailListForBpc
    val casteList: StateFlow<List<CasteEntity>> get() = _casteList
    var villageId: Int = -1
    init {
        villageId = prefRepo.getSelectedVillage().id
        job= CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
                _casteList.value = casteListDao.getAllCasteForLanguage(prefRepo.getAppLanguageId() ?: 2)
                if (prefRepo.isUserBPC()) {
                    val didiList = poorDidiListDao.getAllPoorDidisForVillage(villageId)
                    _didiDetailListForBpc.value = didiList
                } else {
                    val didiList = didiDao.getAllDidisForVillage(villageId)
                    _didiDetailList.value = didiList
                }
            }
        }
    }
    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }

    fun generateFormAPdf(context: Context, callBack: (formGenerated: Boolean, formPath: File?) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val villageEntity = prefRepo.getSelectedVillage()
            val success = if (prefRepo.isUserBPC()) {
                PdfUtils.getFormAPdfForBpc(
                    context = context,
                    villageEntity = villageEntity,
                    didiDetailList = didiDetailListForBpc.value.filter { it.wealth_ranking == WealthRank.POOR.rank && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal && !it.rankingEdit },
                    casteList = casteList.value,
                    completionDate = changeMilliDateToDate(
                        prefRepo.getPref(
                            PREF_WEALTH_RANKING_COMPLETION_DATE_ + villageEntity.id, 0L
                        )
                    ) ?: BLANK_STRING
                )
            } else {
                PdfUtils.getFormAPdf(
                    context = context,
                    villageEntity = villageEntity,
                    didiDetailList = didiDetailList.value.filter { it.wealth_ranking == WealthRank.POOR.rank && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal && !it.rankingEdit },
                    casteList = casteList.value,
                    completionDate = changeMilliDateToDate(
                        prefRepo.getPref(
                            PREF_WEALTH_RANKING_COMPLETION_DATE_ + villageEntity.id, 0L
                        )
                    ) ?: BLANK_STRING
                )
            }
            withContext(Dispatchers.Main) {
                delay(500)
                val path = if (success) PdfUtils.getPdfPath(context = context, formName = FORM_A_PDF_NAME, villageEntity.id) else null
                callBack(success, path)
            }
        }
    }

    fun generateFormBPdf(context: Context, callBack: (formGenerated: Boolean, formPath: File?) -> Unit)  {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val villageEntity = prefRepo.getSelectedVillage()
            val success = if (prefRepo.isUserBPC()) {
                PdfUtils.getFormBPdfForBpc(context, villageEntity = prefRepo.getSelectedVillage(),
                    didiDetailList = didiDetailListForBpc.value.filter { it.forVoEndorsement == 1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal  && !it.patEdit },
                    casteList = casteList.value,
                    completionDate = changeMilliDateToDate(prefRepo.getPref(PREF_PAT_COMPLETION_DATE_+villageEntity.id,0L)) ?: BLANK_STRING)
            } else {
                PdfUtils.getFormBPdf(context, villageEntity = prefRepo.getSelectedVillage(),
                    didiDetailList = didiDetailList.value.filter { it.forVoEndorsement == 1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal  && !it.patEdit },
                    casteList = casteList.value,
                    completionDate = changeMilliDateToDate(prefRepo.getPref(PREF_PAT_COMPLETION_DATE_+villageEntity.id,0L)) ?: BLANK_STRING)
            }
            withContext(Dispatchers.Main) {
                delay(500)
                val path = if (success) PdfUtils.getPdfPath(context = context, formName = FORM_B_PDF_NAME, villageEntity.id) else null
                callBack(success, path)
            }
        }
    }

    fun generateFormCPdf(context: Context, callBack: (formGenerated: Boolean, formPath: File?) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val villageEntity = prefRepo.getSelectedVillage()
            val success = if (prefRepo.isUserBPC()) {
                PdfUtils.getFormCPdfForBpc(context, villageEntity = prefRepo.getSelectedVillage(),
                    didiDetailList = didiDetailListForBpc.value.filter { it.forVoEndorsement == 1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal && it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal },
                    casteList = casteList.value,
                    completionDate = changeMilliDateToDate(prefRepo.getPref(PREF_VO_ENDORSEMENT_COMPLETION_DATE_+villageEntity.id,0L)) ?: BLANK_STRING)
            } else {
                PdfUtils.getFormCPdf(context, villageEntity = prefRepo.getSelectedVillage(),
                    didiDetailList = didiDetailList.value.filter { it.forVoEndorsement == 1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal && it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal },
                    casteList = casteList.value,
                    completionDate = changeMilliDateToDate(prefRepo.getPref(PREF_VO_ENDORSEMENT_COMPLETION_DATE_+villageEntity.id,0L)) ?: BLANK_STRING)
            }
            withContext(Dispatchers.Main) {
                delay(500)
                val path = if (success) PdfUtils.getPdfPath(context = context, formName = FORM_C_PDF_NAME, villageEntity.id) else null
                callBack(success, path)
            }
        }
    }

    fun requestStoragePermission(
        context: Activity,
        viewModel: DigitalFormViewModel,
        requestPermission: () -> Unit
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            when{
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("DigitalFormAScreen", "Permission previously granted")
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    1
                )
            }
            else -> {
                Log.d("requestStoragePermission: ", "permission not granted")
                requestPermission()
            }
        }
        }
    }

}