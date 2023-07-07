package com.patsurvey.nudge.activities

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.compose.runtime.mutableStateOf
import com.patsurvey.nudge.R
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.dao.AnswerDao
import com.patsurvey.nudge.database.dao.DidiDao
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.LocationCoordinates
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class PatDidiSummaryViewModel @Inject constructor(
    val prefRepo: PrefRepo,
    val didiDao: DidiDao,
    val answerDao: AnswerDao,
    val apiService: ApiService
) :
    BaseViewModel() {

    lateinit var outputDirectory: File
    lateinit var cameraExecutor: ExecutorService

    val shouldShowCamera = mutableStateOf(false)

    lateinit var photoUri: Uri
    var shouldShowPhoto = mutableStateOf(false)

    private val _didiEntity = MutableStateFlow(
        DidiEntity(
            id = 0,
            name = "",
            address = "",
            guardianName = "",
            relationship = "",
            castId = 0,
            castName = "",
            cohortId = 0,
            cohortName = "",
            villageId = 0,
            createdDate = System.currentTimeMillis(),
            modifiedDate = System.currentTimeMillis(),
            shgFlag = SHGFlag.NOT_MARKED.value
        )
    )
    val didiEntity: StateFlow<DidiEntity> get() = _didiEntity

    init {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun setCameraExecutor() {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun setUpOutputDirectory(activity: MainActivity) {
        outputDirectory = getOutputDirectory(activity) /*getImagePath(activity)*/
    }

    private fun getImagePath(context: Context): File {
        return File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}")
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
        if (mediaDir != null) {
            if(!mediaDir.exists())
                mediaDir.mkdirs()
        }
        return/* if (mediaDir != null && mediaDir.exists()) mediaDir else */activity.filesDir
    }

    fun saveFilePathInDb(
        photoPath: String,
        locationCoordinates: LocationCoordinates,
        didiEntity: DidiEntity
    ) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val finalPathWithCoordinates =
                "$photoPath|(${locationCoordinates.lat}, ${locationCoordinates.long})"
            didiDao.saveLocalImagePath(path = finalPathWithCoordinates, didiId = didiEntity.id)
        }
    }

    fun getDidiDetails(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _didiEntity.emit(didiDao.getDidi(didiId))
        }
    }

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        TODO("Not yet implemented")
    }

    fun isPatStarted(didiId: Int, callBack:(Boolean) -> Unit) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val answers = answerDao.getAnswerForDidi(TYPE_EXCLUSION, didiId = didiId)
            if (!answers.isNullOrEmpty()){
                callBack(true)
            } else {
                callBack(false)
            }
        }
    }

    fun updateDidiShgFlag(didiId: Int, flagStatus: SHGFlag) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            didiDao.updateDidiShgStatus(didiId = didiId, shgFlag = flagStatus.value)

        }
    }

    fun uploadDidiImage(image: MultipartBody.Part, didiId: RequestBody) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            withContext(Dispatchers.IO){
              try {
                  apiService.uploadDidiImage(image,didiId)
                }   catch (ex:Exception){
                    ex.printStackTrace()
                }

            }
        }
    }
}