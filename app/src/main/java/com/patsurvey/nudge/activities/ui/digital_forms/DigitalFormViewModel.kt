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
import com.patsurvey.nudge.database.CasteEntity
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.PoorDidiEntity
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.DidiStatus
import com.patsurvey.nudge.utils.FORM_A_PDF_NAME
import com.patsurvey.nudge.utils.FORM_B_PDF_NAME
import com.patsurvey.nudge.utils.FORM_C_PDF_NAME
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_KEY_TYPE_STATE_ID
import com.patsurvey.nudge.utils.PREF_PAT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_VO_ENDORSEMENT_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PREF_WEALTH_RANKING_COMPLETION_DATE_
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.PdfUtils
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
    val digitalFormRepository: DigitalFormRepository
):BaseViewModel()  {
    private val _didiDetailList = MutableStateFlow(listOf<DidiEntity>())
    private val _didiDetailListForBpc = MutableStateFlow(listOf<PoorDidiEntity>())
    private val _casteList = MutableStateFlow(listOf<CasteEntity>())
    val didiDetailList: StateFlow<List<DidiEntity>> get() = _didiDetailList
    val didiDetailListForBpc: StateFlow<List<PoorDidiEntity>> get() = _didiDetailListForBpc
    val casteList: StateFlow<List<CasteEntity>> get() = _casteList
    var villageId: Int = -1
    fun getStateId():Int{
        return digitalFormRepository.prefRepo.getPref(PREF_KEY_TYPE_STATE_ID, 4)
    }
    init {
        villageId = digitalFormRepository.getSelectedVillage().id
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO) {
                try {
                    _casteList.value = digitalFormRepository.getAllCasteForLanguage(
                        digitalFormRepository.getAppLanguageId() ?: 2
                    )
                    if (digitalFormRepository.isUserBPC()) {
                        val didiList = digitalFormRepository.getAllPoorDidisForVillage(villageId)
                        didiList.forEach {
                            NudgeLogger.d(
                                "DigitalFormViewModel",
                                "init isUserBPC -> didi.id = ${it.id}, didi.name = ${it.name}"
                            )
                        }
                        _didiDetailListForBpc.value = didiList
                    } else {
                        val didiList = digitalFormRepository.getAllDidisForVillage(villageId)
                        didiList.forEach {
                            NudgeLogger.d(
                                "DigitalFormViewModel",
                                "init -> didi.id = ${it.id}, didi.name = ${it.name}"
                            )
                        }
                        _didiDetailList.value = didiList
                    }
                } catch (ex: Exception) {
                    NudgeLogger.d("DigitalFormViewModel", "init onCatch called")
                    onCatchError(ex)
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
            val villageEntity = digitalFormRepository.getSelectedVillage()
            val success = if (digitalFormRepository.isUserBPC()) {
                NudgeLogger.d(
                    "DigitalFormViewModel",
                    "generateFormAPdf isBpcUser -> villageEntity: ${villageEntity.id}"
                )
                didiDetailList.value.forEach {
                    NudgeLogger.d(
                        "DigitalFormViewModel",
                        "generateFormAPdf isBpcUser -> didi.id = ${it.id}, didi.name = ${it.name}"
                    )
                }
                PdfUtils.getFormAPdfForBpc(
                    context = context,
                    villageEntity = villageEntity,
                    didiDetailList = didiDetailListForBpc.value,
                    casteList = casteList.value,
                    completionDate = changeMilliDateToDate(
                        digitalFormRepository.getPref(
                            PREF_WEALTH_RANKING_COMPLETION_DATE_ + villageEntity.id, 0L
                        )
                    ) ?: BLANK_STRING
                )
            } else {
                NudgeLogger.d("DigitalFormViewModel", "generateFormAPdf -> villageEntity: ${villageEntity.id}")
                didiDetailList.value.forEach {
                    NudgeLogger.d("DigitalFormViewModel", "generateFormAPdf -> didi.id = ${it.id}, didi.name = ${it.name}")
                }
                PdfUtils.getFormAPdf(
                    context = context,
                    villageEntity = villageEntity,
                    didiDetailList = didiDetailList.value,
                    casteList = casteList.value,
                    completionDate = changeMilliDateToDate(
                        digitalFormRepository.getPref(
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
            val villageEntity = digitalFormRepository.getSelectedVillage()
            val success = if (digitalFormRepository.isUserBPC()) {
                NudgeLogger.d(
                    "DigitalFormViewModel",
                    "generateFormBPdf isBpcUser -> villageEntity: ${villageEntity.id}"
                )
                didiDetailList.value.forEach {
                    NudgeLogger.d(
                        "DigitalFormViewModel",
                        "generateFormBPdf isBpcUser -> didi.id = ${it.id}, didi.name = ${it.name}"
                    )
                }
                PdfUtils.getFormBPdfForBpc(
                    context, villageEntity = digitalFormRepository.getSelectedVillage(),
                    didiDetailList = didiDetailListForBpc.value.filter { it.forVoEndorsement == 1 && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal && !it.patEdit },
                    casteList = casteList.value,
                    completionDate = changeMilliDateToDate(
                        digitalFormRepository.getPref(
                            PREF_PAT_COMPLETION_DATE_ + villageEntity.id,
                            0L
                        )
                    ) ?: BLANK_STRING
                )
            } else {
                NudgeLogger.d(
                    "DigitalFormViewModel",
                    "generateFormBPdf -> villageEntity: ${villageEntity.id}"
                )
                didiDetailList.value.forEach {
                    NudgeLogger.d(
                        "DigitalFormViewModel",
                        "generateFormBPdf -> didi.id = ${it.id}, didi.name = ${it.name}"
                    )
                }
                PdfUtils.getFormBPdf(
                    context, villageEntity = digitalFormRepository.getSelectedVillage(),
                    didiDetailList = didiDetailList.value.filter { it.forVoEndorsement == 1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal && !it.patEdit },
                    casteList = casteList.value,
                    completionDate = changeMilliDateToDate(
                        digitalFormRepository.getPref(
                            PREF_PAT_COMPLETION_DATE_ + villageEntity.id,
                            0L
                        )
                    ) ?: BLANK_STRING
                )
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
            val villageEntity = digitalFormRepository.getSelectedVillage()
            val success = if (digitalFormRepository.isUserBPC()) {
                NudgeLogger.d(
                    "DigitalFormViewModel",
                    "generateFormCPdf isBpcUser -> villageEntity: ${villageEntity.id}"
                )
                didiDetailList.value.forEach {
                    NudgeLogger.d(
                        "DigitalFormViewModel",
                        "generateFormCPdf isBpcUser -> didi.id = ${it.id}, didi.name = ${it.name}"
                    )
                }
                PdfUtils.getFormCPdfForBpc(
                    context, villageEntity = digitalFormRepository.getSelectedVillage(),
                    didiDetailList = didiDetailListForBpc.value.filter { it.forVoEndorsement == 1 && it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal },
                    casteList = casteList.value,
                    completionDate = changeMilliDateToDate(
                        digitalFormRepository.getPref(
                            PREF_VO_ENDORSEMENT_COMPLETION_DATE_ + villageEntity.id,
                            0L
                        )
                    ) ?: BLANK_STRING
                )
            } else {
                NudgeLogger.d(
                    "DigitalFormViewModel",
                    "generateFormCPdf -> villageEntity: ${villageEntity.id}"
                )
                didiDetailList.value.forEach {
                    NudgeLogger.d(
                        "DigitalFormViewModel",
                        "generateFormCPdf -> didi.id = ${it.id}, didi.name = ${it.name}"
                    )
                }
                PdfUtils.getFormCPdf(
                    context, villageEntity = digitalFormRepository.getSelectedVillage(),
                    didiDetailList = didiDetailList.value.filter { it.forVoEndorsement == 1 && it.section2Status == PatSurveyStatus.COMPLETED.ordinal && it.voEndorsementStatus == DidiEndorsementStatus.ENDORSED.ordinal && it.activeStatus == DidiStatus.DIDI_ACTIVE.ordinal },
                    casteList = casteList.value,
                    completionDate = changeMilliDateToDate(
                        digitalFormRepository.getPref(
                            PREF_VO_ENDORSEMENT_COMPLETION_DATE_ + villageEntity.id,
                            0L
                        )
                    ) ?: BLANK_STRING
                )
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