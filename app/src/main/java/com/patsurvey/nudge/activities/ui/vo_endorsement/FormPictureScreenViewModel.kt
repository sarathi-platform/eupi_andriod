package com.patsurvey.nudge.activities.ui.vo_endorsement

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.dao.StepsListDao
import com.patsurvey.nudge.database.dao.VillageListDao
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class FormPictureScreenViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val villageListDao: VillageListDao,
    val stepsListDao: StepsListDao
): BaseViewModel() {

    lateinit var outputDirectory: File
    lateinit var cameraExecutor: ExecutorService

    val shouldShowCamera = mutableStateOf(Pair<String, Boolean>("", false))

    val formCPageList =
        mutableStateOf(mutableListOf<Int>())

    val formDPageList =
        mutableStateOf(mutableListOf<Int>())

    val formCImageList =
        mutableStateOf(mutableMapOf<String, String>())


    val formDImageList =
        mutableStateOf(mutableMapOf<String, String>())


    val formsClicked = mutableStateOf(0)

    val retakeImageIndex =
        mutableStateOf(-1)

    lateinit var photoUri: Uri
    var shouldShowPhoto = mutableStateOf(false)

    val pageItemClicked = mutableStateOf("")

//    { formName } _page_ ${ formPictureScreenViewModel.formDPageList.value.size + 1 }

    val imagePath = mutableStateOf("")

    val uri = mutableStateOf (Uri.EMPTY)
//    init {
//        cameraExecutor = Executors.newSingleThreadExecutor()
//    }

    fun setUri(context: Context) {
        uri.value = if (imagePath.value != null ) uriFromFile(context, File(imagePath.value)) else null
    }

    fun setCameraExecutor() {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun setUpOutputDirectory(activity: MainActivity) {
        outputDirectory = /*getOutputDirectory(activity)*/ getImagePath(activity)
    }

    private fun getImagePath(context: Context): File {
        return File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}")
    }

    fun saveFormPath(formPath: String, formName: String){
        prefRepo.savePref("${PREF_FORM_PATH}_$formName", formPath)
    }

    fun markVoEndorsementComplete(villageId: Int, stepId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val existingList = villageListDao.getVillage(villageId).steps_completed
            val updatedCompletedStepsList = mutableListOf<Int>()
            if (!existingList.isNullOrEmpty()) {
                existingList.forEach {
                    updatedCompletedStepsList.add(it)
                }
            }
            updatedCompletedStepsList.add(stepId)
            villageListDao.updateLastCompleteStep(villageId, updatedCompletedStepsList)
            stepsListDao.markStepAsCompleteOrInProgress(stepId, StepStatus.COMPLETED.ordinal,villageId)
            val stepDetails=stepsListDao.getStepForVillage(villageId, stepId)
            if(stepDetails.orderNumber<stepsListDao.getAllSteps().size){
                stepsListDao.markStepAsInProgress((stepDetails.orderNumber+1),
                    StepStatus.INPROGRESS.ordinal,villageId)
            }
            prefRepo.savePref("$VO_ENDORSEMENT_COMPLETE_FOR_VILLAGE_${villageId}", true)
        }
    }

    fun saveVoEndorsementDate() {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val date = dateFormat.format(currentTime)
        prefRepo.savePref(PREF_VO_ENDORSEMENT_COMPLETION_DATE, date)
    }

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

    fun updateFormCImageCount(size: Int) {
        prefRepo.savePref(PREF_FORM_C_PAGE_COUNT, size)
    }
    fun updateFormDImageCount(size: Int) {
        prefRepo.savePref(PREF_FORM_D_PAGE_COUNT, size)
    }


}