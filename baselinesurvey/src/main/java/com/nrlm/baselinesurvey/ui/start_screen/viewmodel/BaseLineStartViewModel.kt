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
import com.nrlm.baselinesurvey.data.domain.EventWriterHelperImpl
import com.nrlm.baselinesurvey.database.entity.DidiInfoEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.model.datamodel.CasteModel
import com.nrlm.baselinesurvey.model.datamodel.SaveAnswerEventOptionItemDto
import com.nrlm.baselinesurvey.model.datamodel.SectionListItem
import com.nrlm.baselinesurvey.ui.Constants.QuestionType
import com.nrlm.baselinesurvey.ui.common_components.SHGFlag
import com.nrlm.baselinesurvey.ui.common_components.common_events.EventWriterEvents
import com.nrlm.baselinesurvey.ui.common_components.common_events.SurveyStateEvents
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.ui.start_screen.domain.use_case.StartSurveyScreenUserCase
import com.nrlm.baselinesurvey.ui.start_screen.presentation.StartSurveyScreenEvents
import com.nrlm.baselinesurvey.utils.BaselineCore
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.LocationCoordinates
import com.nrlm.baselinesurvey.utils.LocationUtil
import com.nrlm.baselinesurvey.utils.findTagForId
import com.nrlm.baselinesurvey.utils.getFileNameFromURL
import com.nrlm.baselinesurvey.utils.tagList
import com.nudge.core.compressImage
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.EventType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.function.Consumer
import javax.inject.Inject

@HiltViewModel
class BaseLineStartViewModel @Inject constructor(
    private val startSurveyScreenUserCase: StartSurveyScreenUserCase,
    private val eventsWriterHelperImpl: EventWriterHelperImpl
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
        DidiInfoEntity.getEmptyDidiInfoEntity()
    )
    val didiEntity: StateFlow<SurveyeeEntity> get() = _didiEntity
    val didiInfo: StateFlow<DidiInfoEntity> get() = _didiInfo

    var isAdharTxtVisible = derivedStateOf {
        isAdharCard.value == 1
    }
    val adharCardState = mutableStateOf(OptionItemEntityState.getEmptyStateObject())

    var sectionDetails: SectionListItem = SectionListItem(
        languageId = 2
    )

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

            is EventWriterEvents.UpdateSectionStatusEvent -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val updateSectionStatusEvent =
                        eventsWriterHelperImpl.createUpdateSectionStatusEvent(
                            event.surveyId,
                            event.sectionId,
                            event.didiId,
                            event.sectionStatus
                        )
                    startSurveyScreenUserCase.eventsWriterUseCase.invoke(
                        events = updateSectionStatusEvent,
                        eventType = EventType.STATEFUL
                    )
                }
            }

            is EventWriterEvents.SaveAnswerEvent -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val saveAnswerEvent =
                        eventsWriterHelperImpl.createSaveAnswerEventForFormTypeQuestion(
                            surveyId = event.surveyId,
                            sectionId = event.sectionId,
                            didiId = event.didiId,
                            questionId = event.questionId,
                            questionType = event.questionType,
                            questionTag = event.questionTag,
                            saveAnswerEventOptionItemDtoList = event.saveAnswerEventOptionItemDtoList
                        )
                    startSurveyScreenUserCase.eventsWriterUseCase.invoke(
                        events = saveAnswerEvent,
                        eventType = EventType.STATEFUL
                    )

                    onEvent(
                        EventWriterEvents.SaveImageUploadEvent(
                            surveyId = event.surveyId,
                            sectionId = event.sectionId,
                            didiId = event.didiId,
                            questionId = event.questionId,
                            questionType = event.questionType,
                            questionTag = event.questionTag
                        )
                    )

                }
            }

            is EventWriterEvents.SaveImageUploadEvent -> {
                CoroutineScope(Dispatchers.IO).launch {
                    writeImageUploadEvent(event.didiId)
                }
            }
        }
    }

    private suspend fun writeImageUploadEvent(didiId: Int) {
        val question = sectionDetails.questionList.first()


        val imageUploadEvent = eventsWriterHelperImpl.createImageUploadEvent(
            didi = didiEntity.value,
            location = didiImageLocation.value,
            filePath = updatedLocalPath.value ?: "",
            userType = startSurveyScreenUserCase.getSurveyeeDetailsUserCase.getUserType()
                ?: BLANK_STRING,
            questionId = question.questionId ?: 0,
            referenceId = didiId.toString() ?: "0",
            sectionDetails = sectionDetails,
            subjectType = "Didi"
        )

        delay(500)
        val compressedDidi = compressImage(
            updatedLocalPath.value,
            BaselineCore.getAppContext(),
            getFileNameFromURL(photoUri.value.path ?: "")
        )
        photoUri.value = File(compressedDidi).toUri()
        startSurveyScreenUserCase.eventsWriterUseCase.writeImageEventIntoLogFile(
            imageUploadEvent ?: Events.getEmptyEvent(), photoUri.value
        )
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

    fun getDidiDetails(didiId: Int, sectionId: Int, surveyId: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val selectedLanguage = startSurveyScreenUserCase.getSectionUseCase.getSelectedLanguage()
            sectionDetails = startSurveyScreenUserCase.getSectionUseCase.invoke(
                sectionId,
                surveyId,
                selectedLanguage
            )

            _didiEntity.emit(startSurveyScreenUserCase.getSurveyeeDetailsUserCase.invoke(didiId))
            startSurveyScreenUserCase.getSurveyeeDetailsUserCase.getDidiIndoDetail(
                didiId
            )?.let {
                _didiInfo.emit(
                    it
                )
            }
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

    fun getCasteListForSelectedLanguage(): List<CasteModel> {
        return startSurveyScreenUserCase.getCasteListUseCase.invoke()
    }

    fun getStateId(): Int {
        return startSurveyScreenUserCase.getSurveyeeDetailsUserCase.getStateId()
    }

    fun addDidiInfoEvent(didi: SurveyeeEntity) {
        val didiInfo = DidiInfoEntity(
            didiId = didi.didiId,
            isAdharCard = isAdharCard.value,
            isVoterCard = isVoterCard.value,
            adharNumber = aadharNumber.value,
            phoneNumber = phoneNumber.value
        )
        val question = sectionDetails.questionList.first()
        val saveAnswerEventOptionItemDtoList = mutableListOf<SaveAnswerEventOptionItemDto>()
        sectionDetails.optionsItemMap[question.questionId]?.filter { it.optionType != QuestionType.Image.name }
            ?.forEach {
                val saveAnswerEventOptionItemDto = SaveAnswerEventOptionItemDto(
                    optionId = it.optionId ?: 0,
                    selectedValue = if (tagList.findTagForId(it.optionTag)
                            .equals("Aadhar", true)
                    ) SHGFlag.fromInt(didiInfo.isAdharCard ?: 0).name
                    else if (tagList.findTagForId(it.optionTag)
                            .equals("Voter", true)
                    ) SHGFlag.fromInt(
                        didiInfo.isVoterCard ?: 0
                    ).name
                    else didiInfo.phoneNumber ?: BLANK_STRING,
                    referenceId = didiInfo.didiId.toString(),
                tag = it.optionTag
            )
            saveAnswerEventOptionItemDtoList.add(saveAnswerEventOptionItemDto)
        }
        onEvent(
            EventWriterEvents.SaveAnswerEvent(
                surveyId = sectionDetails.surveyId,
                sectionId = sectionDetails.surveyId,
                didiId = didiInfo.didiId ?: 0,
                questionId = question.questionId ?: 0,
                questionType = question.type ?: QuestionType.Form.name,
                questionTag = question.tag,
                saveAnswerEventOptionItemDtoList = saveAnswerEventOptionItemDtoList.toList()
            )
        )


    }

}