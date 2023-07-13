package com.patsurvey.nudge.activities

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.patsurvey.nudge.MyApplication.Companion.appScopeLaunch
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
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.USER_BPC
import com.patsurvey.nudge.utils.USER_CRP
import com.patsurvey.nudge.utils.compressImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
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
    var didiImageLocation = mutableStateOf("{0.0,0.0}")

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
        outputDirectory = /*getOutputDirectory(activity)*/ getImagePath(activity)
    }

//    private fun getImagePath(context: Context): File {
//        return File("${context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath}")
//    }

    /*fun setUpOutputDirectory(activity: MainActivity) {
//        outputDirectory = /*getOutputDirectory(activity)*/ getImagePath(activity)
        outputDirectory = getOutputDirectory(activity)
    }*/

    private fun getImagePath(context: Context): File {
        return File("${context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath}")
    }

    fun getOutputDirectory(activity: MainActivity): File {
        val mediaDir = activity.externalCacheDir?.let { file ->
            File(file, activity.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else activity.filesDir
    }

   /* private fun getOutputDirectory(activity: MainActivity): File {
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
        return if (mediaDir != null && mediaDir.exists()) mediaDir else activity.filesDir
    }*/

    fun saveFilePathInDb(
        photoPath: String,
        locationCoordinates: LocationCoordinates,
        didiEntity: DidiEntity
    ) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            didiImageLocation.value = "{${locationCoordinates.lat}, ${locationCoordinates.long}}"
            val finalPathWithCoordinates =
                "$photoPath|(${locationCoordinates.lat}, ${locationCoordinates.long})"
            didiDao.saveLocalImagePath(path = finalPathWithCoordinates, didiId = didiEntity.id)
        }
    }

    fun getDidiDetails(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _didiEntity.emit(didiDao.getDidi(didiId))
            if(!_didiEntity.value.localPath.isNullOrEmpty()){
                photoUri=_didiEntity.value.localPath.toUri()
                shouldShowPhoto.value=true
            }
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
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            didiDao.updateDidiShgStatus(didiId = didiId, shgFlag = flagStatus.value)

        }
    }

    fun uploadDidiImage(context: Context,uri: Uri, didiId: Int,location:String) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            withContext(Dispatchers.IO){
                NudgeLogger.d("PatDidiSummaryViewModel", "uploadDidiImage: $didiId :: $location")
              try {
                  NudgeLogger.d("PatDidiSummaryViewModel", "uploadDidiImage Prev: ${uri.toFile().totalSpace} ")
                  val compressedImageFile = compressImage(uri.toString(),context,uri.toFile().name)
                  val requestFile= RequestBody.create("multipart/form-data".toMediaTypeOrNull(),File(compressedImageFile))
                  val imageFilePart= MultipartBody.Part.createFormData("file",File(compressedImageFile).name,requestFile)
                  val requestDidiId=RequestBody.create("multipart/form-data".toMediaTypeOrNull(),didiId.toString())
                  val requestUserType=RequestBody.create("multipart/form-data".toMediaTypeOrNull(),if(prefRepo.isUserBPC()) USER_BPC else USER_CRP)
                  val requestLocation=RequestBody.create("multipart/form-data".toMediaTypeOrNull(),location)
                  NudgeLogger.d("PatDidiSummaryViewModel", "uploadDidiImage Details: ${requestDidiId.contentType().toString()}")
                  val imageUploadRequest = apiService.uploadDidiImage(imageFilePart,requestDidiId,requestUserType,requestLocation)
                  NudgeLogger.d("PatDidiSummaryViewModel", "uploadDidiImage imageUploadRequest: ${imageUploadRequest.data ?: ""}")
                }   catch (ex:Exception){
                    ex.printStackTrace()
                }

            }
        }
    }
}