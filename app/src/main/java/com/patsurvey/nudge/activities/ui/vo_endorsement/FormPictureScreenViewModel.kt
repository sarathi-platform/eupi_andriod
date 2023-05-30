package com.patsurvey.nudge.activities.ui.vo_endorsement

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.network.model.ErrorModel
import com.patsurvey.nudge.utils.PREF_FORM_PATH
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class FormPictureScreenViewModel @Inject constructor(
    val prefRepo: PrefRepo,
): BaseViewModel() {

    lateinit var outputDirectory: File
    lateinit var cameraExecutor: ExecutorService

    val shouldShowCamera = mutableStateOf(Pair<String, Boolean>("", false))

    lateinit var photoUri: Uri
    var shouldShowPhoto = mutableStateOf(false)

    init {
        cameraExecutor = Executors.newSingleThreadExecutor()
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

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }


}