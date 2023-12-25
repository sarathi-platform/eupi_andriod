package com.nrlm.baselinesurvey.ui.start_screen.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.BaselineApplication.Companion.appScopeLaunch
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.LocationCoordinates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class BaseLineStartViewModel @Inject constructor(
    val surveyeeEntityDao: SurveyeeEntityDao,
) : BaseViewModel() {
    var imagePath = ""
    var tempUri: Uri = Uri.EMPTY
    var photoUri: Uri = Uri.EMPTY
    var shouldShowPhoto = mutableStateOf(false)
    var updatedLocalPath = mutableStateOf(BLANK_STRING)
    var didiImageLocation = mutableStateOf("{0.0,0.0}")
    private val _didiEntity = MutableStateFlow(
        SurveyeeEntity(
            id = 0,
            userId = 101,
            didiId = 1011,
            didiName = "",
            dadaName = "",
            cohortId = 1012,
            cohortName = "",
            houseNo = "",
            villageId = 0,
            villageName = "",
            comment = "",
            score = 0.0,
            crpImageName = "",
            crpImageLocalPath = "",
            ableBodied = "",
            casteId = 0,
            relationship = "",
            surveyStatus = 0,
            movedToThisWeek = false
        )
    )
    val didiEntity: StateFlow<SurveyeeEntity> get() = _didiEntity

    fun getFileName(context: Context, didi: SurveyeeEntity): File {
        val directory = getImagePath(context)
        val filePath = File(
            directory,
            "${didi.id}-${didi.cohortId}-${didi.villageId}_${System.currentTimeMillis()}.png"
        )
        return filePath
    }

    fun getImagePath(context: Context): File {
        return File("${context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath}")
    }

    override fun <T> onEvent(event: T) {
        TODO("Not yet implemented")
    }

    fun saveFilePathInDb(
        photoPath: String,
        locationCoordinates: LocationCoordinates,
        didiEntity: SurveyeeEntity
    ) {
        job = appScopeLaunch(Dispatchers.IO + exceptionHandler) {
            BaselineLogger.d("PatDidiSummaryViewModel", "saveFilePathInDb -> start")

            didiImageLocation.value = "{${locationCoordinates.lat}, ${locationCoordinates.long}}"
            val finalPathWithCoordinates =
                "$photoPath|(${locationCoordinates.lat}, ${locationCoordinates.long})"

            BaselineLogger.d(
                "PatDidiSummaryViewModel",
                "saveFilePathInDb -> didiDao.saveLocalImagePath before = didiId: ${didiEntity.id}, finalPathWithCoordinates: $finalPathWithCoordinates"
            )
            didiEntity.userId?.let {
                surveyeeEntityDao.saveLocalImagePath(
                    path = finalPathWithCoordinates,
                    userId = it
                )
            }

            BaselineLogger.d(
                "PatDidiSummaryViewModel",
                "saveFilePathInDb -> didiDao.saveLocalImagePath after"
            )
        }
    }

    fun getDidiDetails(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _didiEntity.emit(surveyeeEntityDao.getDidi(didiId))
            if (!_didiEntity.value.crpImageLocalPath.isNullOrEmpty()) {
                photoUri = if (didiEntity.value.crpImageLocalPath.contains("|"))
                    didiEntity.value.crpImageLocalPath.split("|")[0].toUri()
                else
                    _didiEntity.value.crpImageLocalPath.toUri()
                shouldShowPhoto.value = true
            }
        }
    }
}