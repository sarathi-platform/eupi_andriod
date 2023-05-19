package com.patsurvey.nudge.activities

import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.R
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.network.model.ErrorModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class PatDidiSummaryViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val didiDao: DidiDao
) :
    BaseViewModel() {

    lateinit var outputDirectory: File
    lateinit var cameraExecutor: ExecutorService

    val shouldShowCamera = mutableStateOf(false)

    lateinit var photoUri: Uri
    var shouldShowPhoto = mutableStateOf(false)

    init {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun setUpOutputDirectory(activity: MainActivity) {
        outputDirectory = getOutputDirectory(activity)
    }

    private fun getOutputDirectory(activity: MainActivity): File {
        val mediaDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(
                "${
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM + "/" + activity.resources.getString(
                            R.string.app_name
                        )
                    )
                }"
            )
        } else {
            activity.externalMediaDirs.firstOrNull()?.let {
                File(it, activity.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else activity.filesDir
    }

    fun saveFilePathInDb(photoPath: String, didiEntity: DidiEntity) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            didiDao.saveLocalImagePath(path = photoPath, didiId = didiEntity.id)
        }
    }
    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }
}