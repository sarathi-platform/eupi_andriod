package com.patsurvey.nudge.activities

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.nudge.core.database.entities.CasteEntity
import com.nudge.core.database.entities.getDependentEventsId
import com.nudge.core.enums.EventName
import com.nudge.core.json
import com.nudge.core.model.getMetaDataDtoFromString
import com.patsurvey.nudge.MyApplication.Companion.appScopeLaunch
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.survey.PatDidiSummaryRepository
import com.patsurvey.nudge.base.BaseViewModel
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.model.dataModel.ErrorModel
import com.patsurvey.nudge.model.dataModel.ErrorModelWithApi
import com.patsurvey.nudge.model.request.DidiImageUploadRequest
import com.patsurvey.nudge.utils.AbleBodiedFlag
import com.patsurvey.nudge.utils.ApiType
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.LocationCoordinates
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.SUCCESS
import com.patsurvey.nudge.utils.USER_BPC
import com.patsurvey.nudge.utils.USER_CRP
import com.patsurvey.nudge.utils.compressImage
import com.patsurvey.nudge.utils.getFileNameFromURL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
   val patDidiSummaryRepository: PatDidiSummaryRepository
) : BaseViewModel() {

    lateinit var outputDirectory: File
    var cameraExecutor: ExecutorService

    val shouldShowCamera = mutableStateOf(false)

    var photoUri: Uri = Uri.EMPTY

    var tempUri: Uri = Uri.EMPTY
    var shouldShowPhoto = mutableStateOf(false)
    var didiImageLocation = mutableStateOf("{0.0,0.0}")
    var updatedLocalPath = mutableStateOf(BLANK_STRING)
    private var castList : List<CasteEntity> = emptyList()
    var imagePath = ""

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
            shgFlag = SHGFlag.NOT_MARKED.value,
            ableBodiedFlag = AbleBodiedFlag.NOT_MARKED.value
        )
    )
    val didiEntity: StateFlow<DidiEntity> get() = _didiEntity

    init {
        cameraExecutor = Executors.newSingleThreadExecutor()
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            castList = patDidiSummaryRepository.getAllCasteForLanguage() ?: emptyList()
        }
    }

    fun setCameraExecutor() {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun setUpOutputDirectory(activity: MainActivity) {
        outputDirectory = getImagePath(activity)
    }

    fun getImagePath(context: Context): File {
        return File("${context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath}")
    }

    fun getOutputDirectory(activity: MainActivity): File {
        val mediaDir = activity.externalCacheDir?.let { file ->
            File(file, activity.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else activity.filesDir
    }

    fun saveFilePathInDb(
        uri: Uri,
        photoPath: String,
        locationCoordinates: LocationCoordinates,
        didiEntity: DidiEntity,
        isActivityRestart: Boolean
    ) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            NudgeLogger.d("PatDidiSummaryViewModel", "saveFilePathInDb -> start")
            if (isActivityRestart) {
                delay(400)
            }
            didiImageLocation.value = "{${locationCoordinates.lat}, ${locationCoordinates.long}}"
            val finalPathWithCoordinates =
                "$photoPath|(${locationCoordinates.lat}, ${locationCoordinates.long})"

            NudgeLogger.d("PatDidiSummaryViewModel", "saveFilePathInDb -> didiDao.saveLocalImagePath before = didiId: ${didiEntity.id}, finalPathWithCoordinates: $finalPathWithCoordinates")
            patDidiSummaryRepository.saveDidiLocalImagePath(finalPathWithCoordinates,didiEntity.id)
            NudgeLogger.d("PatDidiSummaryViewModel", "saveFilePathInDb -> didiDao.saveLocalImagePath after")
            val selectedTolaEntity =
                patDidiSummaryRepository.getTolaFromServerId(didiEntity.cohortId)


            val payload = DidiImageUploadRequest.getRequestObjectForDidiUploadImage(
                didi = didiEntity,
                location = didiImageLocation.value,
                filePath = photoPath ?: "",
                userType = if (patDidiSummaryRepository.prefRepo.isUserBPC()) USER_BPC else USER_CRP,
                tolaServerId = selectedTolaEntity?.serverId,
                cohortdeviceId = selectedTolaEntity?.localUniqueId
            ).json()

            var imageUploadEvent = patDidiSummaryRepository.createImageUploadEvent(
                payload = payload,
                payloadlocalId = didiEntity.localUniqueId,
                mobileNumber = patDidiSummaryRepository.prefRepo.getMobileNumber(),
                userID = patDidiSummaryRepository.prefRepo.getUserId(),
                eventName = if (patDidiSummaryRepository.prefRepo.isUserBPC()) EventName.BPC_IMAGE else EventName.CRP_IMAGE,
            )
            val dependsOn = patDidiSummaryRepository.createEventDependency(
                didiEntity,
                if (patDidiSummaryRepository.prefRepo.isUserBPC()) EventName.BPC_IMAGE else EventName.CRP_IMAGE,
                imageUploadEvent
            )
            val metadata = imageUploadEvent.metadata?.getMetaDataDtoFromString()
            val updatedMetaData = metadata?.copy(depends_on = dependsOn.getDependentEventsId())
            imageUploadEvent = imageUploadEvent.copy(
                metadata = updatedMetaData?.json()
            )

            delay(500)
            val compressedDidi = compressImage(
                photoPath,
                NudgeCore.getAppContext(),
                getFileNameFromURL(uri.path ?: "")
            )
            patDidiSummaryRepository.uri = File(compressedDidi).toUri()
            patDidiSummaryRepository.writeImageEventIntoLogFile(imageUploadEvent, dependsOn)
        }
    }

    fun getDidiDetails(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _didiEntity.emit(patDidiSummaryRepository.getDidiFromDB(didiId))
            if(!_didiEntity.value.localPath.isNullOrEmpty()){
                photoUri=if (didiEntity.value.localPath.contains("|"))
                    didiEntity.value.localPath.split("|")[0].toUri()
                else
                    _didiEntity.value.localPath.toUri()
                shouldShowPhoto.value=true
            }
        }
    }

    override fun onServerError(error: ErrorModel?) {
        /*TODO("Not yet implemented")*/
    }

    override fun onServerError(errorModel: ErrorModelWithApi?) {
        NudgeLogger.e(
            "PatDidiSummaryViewModel",
            "onServerError -> errorModel: ${errorModel.toString()}"
        )
    }

    fun getCastName(castId : Int) : String{
        var castName = ""
        for(cast in castList){
            if(castId == cast.id)
                castName = cast.casteName
        }
        return castName
    }

    fun updateDidiShgFlag(didiId: Int, flagStatus: SHGFlag) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            patDidiSummaryRepository.updateDidiSHGFlag(didiId = didiId,shgFlag = flagStatus.value)
            addDidiFlagEvent()
        }
    }

    fun updateDidiAbleBodiedFlag(didiId: Int, flagStatus: AbleBodiedFlag) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            patDidiSummaryRepository.updateDidiAbleBodiedFlag(didiId = didiId, ableBodiedFlag = flagStatus.value)
            addDidiFlagEvent(true)
        }
    }

    fun uploadDidiImage(context: Context, uri: String, didiId: Int, location: String) {
        if (!isSyncEnabled(prefRepo = patDidiSummaryRepository.prefRepo)) {
            return
        }
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            withContext(Dispatchers.IO) {
                try {
                       NudgeLogger.d("PatDidiSummaryViewModel", "uploadDidiImage: $didiId :: $location :: $uri")
                       val compressedImageFile =
                               compressImage(uri.toString(), context, getFileNameFromURL(uri))
                       NudgeLogger.d(
                           "PatDidiSummaryViewModel",
                           "uploadDidiImage Prev: ${ File(compressedImageFile).totalSpace} "
                       )
                       val requestFile = RequestBody.create(
                           "multipart/form-data".toMediaTypeOrNull(),
                           File(compressedImageFile)
                       )
                       val imageFilePart = MultipartBody.Part.createFormData(
                           "file",
                           File(compressedImageFile).name,
                           requestFile
                       )
                       val requestDidiId = RequestBody.create(
                           "multipart/form-data".toMediaTypeOrNull(),
                           didiId.toString()
                       )
                       val requestUserType = RequestBody.create(
                           "multipart/form-data".toMediaTypeOrNull(),
                           if (patDidiSummaryRepository.prefRepo.isUserBPC()) USER_BPC else USER_CRP
                       )
                       val requestLocation =
                           RequestBody.create("multipart/form-data".toMediaTypeOrNull(), location)
                       NudgeLogger.d(
                           "PatDidiSummaryViewModel",
                           "uploadDidiImage Details: ${requestDidiId.contentType().toString()}"
                       )

                    val imageUploadRequest = patDidiSummaryRepository.uploadDidiImage(
                        image = imageFilePart,
                        didiId = requestDidiId,
                        userType = requestUserType,
                        location = requestLocation
                    )
                    NudgeLogger.d(
                               "PatDidiSummaryViewModel",
                    "uploadDidiImage imageUploadRequest status: ${imageUploadRequest.status}  data: ${imageUploadRequest.data ?: ""}"
                    )
                    if (imageUploadRequest.status.equals(SUCCESS, true)) {
                        patDidiSummaryRepository.updateNeedToPostImage(
                            didiId = didiId,
                            needsToPostImage = false
                        )
                    } else {
                        patDidiSummaryRepository.updateNeedToPostImage(
                            didiId = didiId,
                            needsToPostImage = true
                        )
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    patDidiSummaryRepository.updateNeedToPostImage(
                        didiId = didiId,
                        needsToPostImage = true
                    )
                    onCatchError(ex, ApiType.DIDI_IMAGE_UPLOAD_API)
                }

            }
        }
    }

    fun getFileName(context: Context, didi: DidiEntity): File {
        val directory = getImagePath(context)
        val filePath = File(directory, "${didi.id}-${didi.cohortId}-${didi.villageId}_${System.currentTimeMillis()}.png")
        Log.d("TAG", "getFileName: ${filePath}")
        return filePath
    }

    fun setNeedToPostImage(needToPostImage: Boolean) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            patDidiSummaryRepository.updateNeedToPostImage(didiId = didiEntity.value.id, needsToPostImage = needToPostImage)
        }
    }

    fun addDidiFlagEvent(isAbleBodiedFlag: Boolean = false) {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val villageId = patDidiSummaryRepository.prefRepo.getSelectedVillage().id

            val didiEntity = patDidiSummaryRepository.getDidiFromDB(didiId = didiEntity.value.id)

            val tolaDeviceIdMap: Map<Int, String> =
                patDidiSummaryRepository.getTolaDeviceIdMap(
                    villageId,
                    patDidiSummaryRepository.tolaDao
                )

            val addDidiFlagEditEventList =
                if (isAbleBodiedFlag) {
                    patDidiSummaryRepository.createAbleBodiedFlagEvent(
                        didiEntity = didiEntity,
                        tolaDeviceIdMap = tolaDeviceIdMap,
                        mobileNumber = patDidiSummaryRepository.prefRepo.getMobileNumber()
                            ?: BLANK_STRING,
                        userID = patDidiSummaryRepository.prefRepo.getUserId()
                    )
                } else {
                    patDidiSummaryRepository.createShgFlagEvent(
                        didiEntity = didiEntity,
                        tolaDeviceIdMap = tolaDeviceIdMap,
                        mobileNumber = patDidiSummaryRepository.prefRepo.getMobileNumber()
                            ?: BLANK_STRING,
                        userID = patDidiSummaryRepository.prefRepo.getUserId()
                    )
                }


            patDidiSummaryRepository.saveEventToMultipleSources(addDidiFlagEditEventList, listOf())

        }

    }


    fun saveTempCrpFilePath(filePath: String) {
        patDidiSummaryRepository.saveTempImagePath(filePath)
    }

    fun getTempCrpFilePath() = patDidiSummaryRepository.getTempImagePath()


}