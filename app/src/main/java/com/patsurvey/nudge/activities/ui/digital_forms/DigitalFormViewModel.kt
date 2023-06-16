package com.patsurvey.nudge.activities.ui.digital_forms

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.network.model.ErrorModelWithApi
import com.patsurvey.nudge.utils.DidiEndorsementStatus
import com.patsurvey.nudge.utils.FORM_A_PDF_NAME
import com.patsurvey.nudge.utils.FORM_B_PDF_NAME
import com.patsurvey.nudge.utils.FORM_C_PDF_NAME
import com.patsurvey.nudge.utils.PREF_PAT_COMPLETION_DATE
import com.patsurvey.nudge.utils.PREF_VO_ENDORSEMENT_COMPLETION_DATE
import com.patsurvey.nudge.utils.PREF_WEALTH_RANKING_COMPLETION_DATE
import com.patsurvey.nudge.utils.PatSurveyStatus
import com.patsurvey.nudge.utils.PdfUtils
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
):BaseViewModel()  {
    private val _didiDetailList = MutableStateFlow(listOf<DidiEntity>())
    val didiDetailList: StateFlow<List<DidiEntity>> get() = _didiDetailList
    var villageId: Int = -1
    init {
        villageId = prefRepo.getSelectedVillage().id
        job= CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
                _didiDetailList.emit(didiDao.getAllDidisForVillage(villageId))
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

    fun requestStoragePermission(
        context: Activity,
        viewModel: DigitalFormViewModel,
        requestPermission: () -> Unit
    ) {
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
                Log.d("requestCameraPermission: ", "permission not granted")
                requestPermission()
            }
        }
    }

}