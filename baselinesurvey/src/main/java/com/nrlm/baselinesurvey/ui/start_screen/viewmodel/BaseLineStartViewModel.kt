package com.nrlm.baselinesurvey.ui.start_screen.viewmodel

import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.BaselineApplication.Companion.appScopeLaunch
import com.nrlm.baselinesurvey.activity.MainActivity
import com.nrlm.baselinesurvey.base.BaseViewModel
import com.nrlm.baselinesurvey.database.entity.DidiIntoEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.model.datamodel.CasteModel
import com.nrlm.baselinesurvey.ui.common_components.common_events.SurveyStateEvents
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.ui.start_screen.domain.use_case.StartSurveyScreenUserCase
import com.nrlm.baselinesurvey.ui.start_screen.presentation.StartSurveyScreenEvents
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.LocationCoordinates
import com.nrlm.baselinesurvey.utils.LocationUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.function.Consumer
import javax.inject.Inject

@HiltViewModel
class BaseLineStartViewModel @Inject constructor(
    private val startSurveyScreenUserCase: StartSurveyScreenUserCase
) : BaseViewModel() {
    var imagePath = ""

    var tempUri: Uri = Uri.EMPTY
    var photoUri: MutableState<Uri> = mutableStateOf(Uri.EMPTY)

    var shouldShowPhoto = mutableStateOf(false)

    var updatedLocalPath = mutableStateOf(BLANK_STRING)

    var didiImageLocation = mutableStateOf("{0.0,0.0}")
    val isAdharCard =
        mutableStateOf(-1)

    val aadharNumber =
        mutableStateOf(BLANK_STRING)

    val phoneNumber =
        mutableStateOf(BLANK_STRING)

    val isVoterCard =
        mutableStateOf(-1)

    private val _didiEntity = MutableStateFlow(
        SurveyeeEntity.getEmptySurveyeeEntity()
    )
    private val _didiInfo = MutableStateFlow(
        DidiIntoEntity.getEmptyDidiIntoEntity()
    )
    val didiEntity: StateFlow<SurveyeeEntity> get() = _didiEntity
    val didiInfo: StateFlow<DidiIntoEntity> get() = _didiInfo

    var isAdharTxtVisible = derivedStateOf {
        isAdharCard.value == 1
    }
    val adharCardState = mutableStateOf(OptionItemEntityState.getEmptyStateObject())

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
        when (event) {
            is StartSurveyScreenEvents.SaveImagePathForSurveyee -> {
                photoUri.value = tempUri
                handleImageCapture(
                    photoPath = imagePath,
                    context = (event.context as MainActivity),
                    didiEntity.value,
                )
            }
            is SurveyStateEvents.UpdateDidiSurveyStatus -> {
                viewModelScope.launch(Dispatchers.IO) {
                    startSurveyScreenUserCase.updateSurveyStateUseCase.invoke(
                        event.didiId,
                        event.didiSurveyState
                    )
                    startSurveyScreenUserCase.updateSurveyStateUseCase.saveDidiInfoInDB(event.didiInfo)
                }
            }
        }
    }

    private fun saveFilePathInDb(
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

            startSurveyScreenUserCase.saveSurveyeeImagePathUseCase.invoke(didiEntity, finalPathWithCoordinates)

            BaselineLogger.d(
                "PatDidiSummaryViewModel",
                "saveFilePathInDb -> didiDao.saveLocalImagePath after"
            )
        }
    }

    fun getDidiDetails(didiId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            _didiEntity.emit(startSurveyScreenUserCase.getSurveyeeDetailsUserCase.invoke(didiId))
            _didiInfo.emit(
                startSurveyScreenUserCase.getSurveyeeDetailsUserCase.getDidiIndoDetail(
                    didiId
                )
            )
            if (!_didiEntity.value.crpImageLocalPath.isNullOrEmpty()) {
                photoUri.value = if (didiEntity.value.crpImageLocalPath.contains("|"))
                    didiEntity.value.crpImageLocalPath.split("|")[0].toUri()
                else
                    _didiEntity.value.crpImageLocalPath.toUri()
                shouldShowPhoto.value = true
            }
            isAdharCard.value = didiInfo.value?.isAdharCard ?: -1
            adharCardState.value = adharCardState.value.copy(showQuestion = isAdharTxtVisible.value)
            phoneNumber.value = didiInfo.value?.phoneNumber ?: BLANK_STRING
            isVoterCard.value = didiInfo.value?.isVoterCard ?: -1
            aadharNumber.value = didiInfo.value?.adharNumber ?: BLANK_STRING
        }
    }

    private fun handleImageCapture(
        photoPath: String,
        context: Activity,
        didiEntity: SurveyeeEntity,
    ) {

        updatedLocalPath.value = photoPath
        shouldShowPhoto.value = true
        var location = LocationCoordinates(0.0, 0.0)

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            var locationByGps: Location? = null
            var locationByNetwork: Location? = null
            val gpsConsumer = Consumer<Location> { gpsLocation ->
                if (gpsLocation != null) {
                    locationByGps = gpsLocation
                    location = LocationCoordinates(
                        locationByGps?.latitude ?: 0.0,
                        locationByGps?.longitude ?: 0.0

                    )
                }
            }
            val networkConsumer = Consumer<Location> { networkLocation ->
                if (networkLocation != null) {
                    locationByNetwork = networkLocation
                    location = LocationCoordinates(
                        locationByNetwork?.latitude ?: 0.0,
                        locationByNetwork?.longitude ?: 0.0

                    )
                }
            }
            LocationUtil.getLocation(
                context = context,
                gpsConsumer,
                networkConsumer
            )
        } else {
            var locationByGps: Location? = null
            var locationByNetwork: Location? = null

            val gpsLocationListener: LocationListener = object : LocationListener {
                override fun onLocationChanged(gpsLocation: Location) {
                    locationByGps = gpsLocation
                    location = LocationCoordinates(
                        locationByGps?.latitude ?: 0.0,
                        locationByGps?.longitude ?: 0.0

                    )
                }

                override fun onStatusChanged(
                    provider: String,
                    status: Int,
                    extras: Bundle
                ) {
                }

                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            val networkLocationListener: LocationListener = object :
                LocationListener {
                override fun onLocationChanged(networkLocation: Location) {
                    locationByNetwork = networkLocation
                    location = LocationCoordinates(
                        locationByNetwork?.latitude ?: 0.0,
                        locationByNetwork?.longitude ?: 0.0

                    )
                }

                override fun onStatusChanged(
                    provider: String,
                    status: Int,
                    extras: Bundle
                ) {
                }

                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }
            LocationUtil.getLocation(
                context = context,
                gpsLocationListener,
                networkLocationListener
            )
        }

        BaselineLogger.d(
            "PatDidiSummaryScreen",
            "handleImageCapture -> viewModal.saveFilePathInDb called"
        )


        saveFilePathInDb(photoPath, location, didiEntity = didiEntity)
    }

    fun getCasteList(): List<CasteModel> {
        return startSurveyScreenUserCase.getCasteListUseCase.invoke()
    }

}