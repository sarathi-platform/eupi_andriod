package com.nrlm.baselinesurvey.ui.start_screen.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.database.entity.DidiInfoEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.ui.common_components.BlueButtonWithIcon
import com.nrlm.baselinesurvey.ui.common_components.ButtonOutline
import com.nrlm.baselinesurvey.ui.common_components.DialogComponent
import com.nrlm.baselinesurvey.ui.common_components.DoubleButtonBox
import com.nrlm.baselinesurvey.ui.common_components.EditTextWithTitleComponent
import com.nrlm.baselinesurvey.ui.common_components.LoaderComponent
import com.nrlm.baselinesurvey.ui.common_components.YesNoButtonComponent
import com.nrlm.baselinesurvey.ui.common_components.common_events.EventWriterEvents
import com.nrlm.baselinesurvey.ui.common_components.common_events.SurveyStateEvents
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState
import com.nrlm.baselinesurvey.ui.splash.presentaion.LoaderEvent
import com.nrlm.baselinesurvey.ui.start_screen.viewmodel.BaseLineStartViewModel
import com.nrlm.baselinesurvey.ui.theme.defaultBottomBarPadding
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.languageItemActiveBg
import com.nrlm.baselinesurvey.ui.theme.smallTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.textColorDark50
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.openSettings
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nrlm.baselinesurvey.utils.states.SurveyState
import com.nrlm.baselinesurvey.utils.uriFromFile
import com.nudge.core.Core
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import java.io.File

@SuppressLint("StateFlowValueCalledInComposition", "UnrememberedMutableState")
@Composable
fun BaseLineStartScreen(
    navController: NavHostController,
    baseLineStartViewModel: BaseLineStartViewModel,
    didiId: Int,
    surveyId: Int,
    sectionId: Int
) {
    val localContext = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val shouldRequestPermission = remember {
        mutableStateOf(false)
    }

    val didi = baseLineStartViewModel.didiEntity
    LaunchedEffect(key1 = true) {
        baseLineStartViewModel.onEvent(LoaderEvent.UpdateLoaderState(true))
        baseLineStartViewModel.getDidiDetails(
            didiId = didiId,
            sectionId = sectionId,
            surveyId = surveyId,
            false,
            localContext
        )
        delay(200)
        baseLineStartViewModel.onEvent(LoaderEvent.UpdateLoaderState(false))
    }


    //val didiInfoDetail = baseLineStartViewModel.didiInfo

    val isContinueButtonActive =
        derivedStateOf {
            (baseLineStartViewModel.photoUri.value != Uri.EMPTY) && (baseLineStartViewModel.isVoterCard.value != -1) && (baseLineStartViewModel.phoneNumber.value.length == 10) &&
                    ((baseLineStartViewModel.isAdharCard.value != -1))
        }



    BackHandler {
        navController.popBackStack()
    }
    LoaderComponent(
        visible = baseLineStartViewModel.loaderState.value.isLoaderVisible,
    )
    if (!baseLineStartViewModel.loaderState.value.isLoaderVisible) {
        Scaffold(modifier = Modifier
            .fillMaxSize(),
            bottomBar = {
                DoubleButtonBox(
                    modifier = Modifier
                        .shadow(10.dp),
                    positiveButtonText = stringResource(R.string.save),
                    negativeButtonText = stringResource(id = R.string.go_back_text),
                    isPositiveButtonActive = isContinueButtonActive.value,
                    negativeButtonRequired = false,
                    positiveButtonOnClick = {
                        baseLineStartViewModel.addDidiInfoEvent(didi.value)
                        baseLineStartViewModel.onEvent(
                            StartSurveyScreenEvents.SaveDidiInfoInDbEvent(baseLineStartViewModel.didiInfo.value)
                        )
                        updateDidiDetails(didi, baseLineStartViewModel)
                        navController.popBackStack()
//                    navController.navigate("$SECTION_SCREEN_ROUTE_NAME/$didiId/$surveyId")
                    },
                    negativeButtonOnClick = {
                        navController.popBackStack()
//                    navigateBackToSurveyeeListScreen(navController)
                    }
                )
            }
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = it.calculateTopPadding() + 6.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (shouldRequestPermission.value) {
                    DialogComponent(
                        title = stringResource(R.string.permission_required_prompt_title),
                        message = stringResource(R.string.permission_dialog_prompt_message),
                        setShowDialog = {
                            shouldRequestPermission.value = it
                        }
                    ) {
                        openSettings(localContext)
                    }
                }
                TextDetails(title = stringResource(R.string.didi_title), data = didi.value.didiName)
                TextDetails(title = stringResource(R.string.dada_title), data = didi.value.dadaName)
                TextDetails(
                    title = stringResource(R.string.caste_titile),
                    data = getCasteName(didi.value.casteId, baseLineStartViewModel)
                )
                YesNoButtonComponent(
                    defaultValue = baseLineStartViewModel.isAdharCard.value,
                    title = if (baseLineStartViewModel.getStateId() == 4) stringResource(R.string.aadhar_card_titile_assam) else stringResource(
                        R.string.aadhar_card_titile
                    )
                ) {
                    baseLineStartViewModel.isAdharCard.value = it
                    baseLineStartViewModel.adharCardState.value =
                        baseLineStartViewModel.adharCardState.value.copy(showQuestion = baseLineStartViewModel.isAdharTxtVisible.value)
                    updateDidiDetails(didi, baseLineStartViewModel)
                    //  (baseLineStartViewModel.photoUri.value != Uri.EMPTY) && (baseLineStartViewModel.isVoterCard.value != -1) && (baseLineStartViewModel.phoneNumber.value.length == 10) && (baseLineStartViewModel.isAdharCard.value != -1)
                }

                /*EditTextWithTitleComponent(
                    defaultValue = baseLineStartViewModel.aadharNumber.value,
                    title = "Enter Didi's aadhar number",
                    isOnlyNumber = true,
                    showQuestion = baseLineStartViewModel.adharCardState.value,
                    maxLength = 12
                ) {
                    baseLineStartViewModel.aadharNumber.value = it
                }*/
                Spacer(modifier = Modifier.height(8.dp))

                Spacer(modifier = Modifier.height(8.dp))
                YesNoButtonComponent(
                    defaultValue = baseLineStartViewModel.isVoterCard.value,
                    title = if (baseLineStartViewModel.getStateId() == 4) stringResource(R.string.voter_card_title_assam) else stringResource(
                        R.string.voter_card_title
                    )
                ) {
                    baseLineStartViewModel.isVoterCard.value = it
                    updateDidiDetails(didi, baseLineStartViewModel)
                }
                Spacer(modifier = Modifier.height(10.dp))
                EditTextWithTitleComponent(
                    defaultValue = baseLineStartViewModel.phoneNumber.value,
                    title = stringResource(R.string.phone_number_title),
                    showQuestion = OptionItemEntityState.getEmptyStateObject()
                        .copy(showQuestion = true),
                    isOnlyNumber = true,
                    maxLength = 10,
                    onInfoButtonClicked = {}
                ) {
                    baseLineStartViewModel.phoneNumber.value = it
                    updateDidiDetails(didi, baseLineStartViewModel)
                }
                Spacer(modifier = Modifier.height(10.dp))

                var hasImage by remember {
                    mutableStateOf(false)
                }
                val cameraLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicture(),
                    onResult = { success ->
                        hasImage = success
                        BaselineLogger.d(
                            "BaseLineStartScreen",
                            "rememberLauncherForActivityResult -> onResult = success: $success"
                        )
                        if (success) {
                            if (baseLineStartViewModel.tempUri == Uri.EMPTY) {
                                baseLineStartViewModel.imagePath =
                                    baseLineStartViewModel.getTempFilePath()
                                val uri =
                                    uriFromFile(localContext, File(baseLineStartViewModel.imagePath))
                                baseLineStartViewModel.tempUri = uri
                                baseLineStartViewModel.getDidiDetails(
                                    didiId,
                                    sectionId = sectionId,
                                    surveyId,
                                    true,
                                    localContext
                                )
                                BaselineLogger.e(
                                    "CameraIssue",
                                    "ImagePath ${baseLineStartViewModel.imagePath}"
                                )
                            } else {
                                baseLineStartViewModel.onEvent(
                                    StartSurveyScreenEvents.SaveImagePathForSurveyee(
                                        localContext
                                    )
                                )
                            }
                        } else {
                            baseLineStartViewModel.shouldShowPhoto.value =
                                baseLineStartViewModel.photoUri.value != Uri.EMPTY
                        }
                    }
                )
                if (baseLineStartViewModel.shouldShowPhoto.value) {
                    AsyncImage(
                        model = baseLineStartViewModel.photoUri.value,
                        contentDescription = null,
                        modifier = Modifier
                            .height((screenHeight / 2).dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                languageItemActiveBg,
                                shape = RoundedCornerShape(6.dp)
                            ),
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .height((screenHeight / 2).dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                languageItemActiveBg,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                    when {
                                        ContextCompat.checkSelfPermission(
                                            localContext as Activity,
                                            Manifest.permission.CAMERA
                                        ) == PackageManager.PERMISSION_GRANTED
                                                && ContextCompat.checkSelfPermission(
                                            localContext as Activity,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        ) == PackageManager.PERMISSION_GRANTED
                                                && ContextCompat.checkSelfPermission(
                                            localContext as Activity,
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                        ) == PackageManager.PERMISSION_GRANTED -> {
                                            BaselineLogger.d(
                                                "PatImagePreviewScreen",
                                                "Permission previously granted"
                                            )

                                            val imageFile =
                                                baseLineStartViewModel.getFileName(
                                                    localContext,
                                                    didi.value
                                                )
                                            baseLineStartViewModel.imagePath =
                                                imageFile.absolutePath
                                            val uri = uriFromFile(localContext, imageFile)
                                            baseLineStartViewModel.tempUri = uri
                                            baseLineStartViewModel.saveTempFilePath(
                                                baseLineStartViewModel.imagePath
                                            )
                                            cameraLauncher.launch(uri)
                                        }

                                        ActivityCompat.shouldShowRequestPermissionRationale(
                                            localContext as Activity,
                                            Manifest.permission.CAMERA
                                        ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                            localContext as Activity,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                            localContext as Activity,
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                        ) -> {
                                            BaselineLogger.d(
                                                "PatImagePreviewScreen",
                                                "Show camera permissions dialog"
                                            )
                                            ActivityCompat.requestPermissions(
                                                localContext as Activity,
                                                arrayOf(
                                                    Manifest.permission.CAMERA,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                                ),
                                                1
                                            )
                                        }

                                        else -> {
                                            BaselineLogger.d(
                                                "requestCameraPermission: ",
                                                "permission not granted"
                                            )
                                            shouldRequestPermission.value = true
                                        }
                                    }
                                } else {
                                    when {
                                        ContextCompat.checkSelfPermission(
                                            localContext as Activity,
                                            Manifest.permission.CAMERA
                                        ) == PackageManager.PERMISSION_GRANTED -> {
                                            BaselineLogger.d(
                                                "PatImagePreviewScreen",
                                                "Permission previously granted"
                                            )

                                            val imageFile =
                                                baseLineStartViewModel.getFileName(
                                                    localContext,
                                                    didi.value
                                                )
                                            baseLineStartViewModel.imagePath =
                                                imageFile.absolutePath
                                            val uri = uriFromFile(localContext, imageFile)
                                            baseLineStartViewModel.tempUri = uri
                                            baseLineStartViewModel.saveTempFilePath(
                                                baseLineStartViewModel.imagePath
                                            )
                                            cameraLauncher.launch(uri)
                                        }

                                        ActivityCompat.shouldShowRequestPermissionRationale(
                                            localContext as Activity,
                                            Manifest.permission.CAMERA
                                        ) -> {
                                            BaselineLogger.d(
                                                "PatImagePreviewScreen",
                                                "Show camera permissions dialog"
                                            )
                                            ActivityCompat.requestPermissions(
                                                localContext as Activity,
                                                arrayOf(
                                                    Manifest.permission.CAMERA,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                                ),
                                                1
                                            )
                                        }

                                        else -> {
                                            BaselineLogger.d(
                                                "requestCameraPermission: ",
                                                "permission not granted"
                                            )
                                            shouldRequestPermission.value = true
                                        }
                                    }
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.photo_camera_icon),
                                contentDescription = null,
                                tint = textColorDark50
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stringResource(R.string.no_photo_available_text),
                                style = smallTextStyle,
                                color = textColorDark50
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                if (baseLineStartViewModel.shouldShowPhoto.value) {
                    ButtonOutline(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        buttonTitle = stringResource(id = R.string.retake_photo_text)
                    ) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            when {
                                ContextCompat.checkSelfPermission(
                                    localContext as Activity,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                                        && ContextCompat.checkSelfPermission(
                                    localContext as Activity,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) == PackageManager.PERMISSION_GRANTED
                                        && ContextCompat.checkSelfPermission(
                                    localContext as Activity,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ) == PackageManager.PERMISSION_GRANTED -> {
                                    BaselineLogger.d(
                                        "PatImagePreviewScreen",
                                        "Permission previously granted"
                                    )

                                    val imageFile =
                                        baseLineStartViewModel.getFileName(localContext, didi.value)
                                    baseLineStartViewModel.imagePath = imageFile.absolutePath
                                    val uri = uriFromFile(localContext, imageFile)
                                    BaselineLogger.d(
                                        "PatDidiSummaryScreen",
                                        "Retake Photo button Clicked: $uri"
                                    )
                                    baseLineStartViewModel.tempUri = uri
//                                patDidiSummaryViewModel.photoUri = uri
                                    baseLineStartViewModel.saveTempFilePath(
                                        baseLineStartViewModel.imagePath
                                    )
                                    cameraLauncher.launch(uri)
                                    baseLineStartViewModel.shouldShowPhoto.value = false
                                }

                                ActivityCompat.shouldShowRequestPermissionRationale(
                                    localContext as Activity,
                                    Manifest.permission.CAMERA
                                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                    localContext as Activity,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                    localContext as Activity,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ) -> {
                                    BaselineLogger.d(
                                        "PatImagePreviewScreen",
                                        "Show camera permissions dialog"
                                    )
                                    ActivityCompat.requestPermissions(
                                        localContext as Activity,
                                        arrayOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                        ),
                                        1
                                    )
                                }

                                else -> {
                                    BaselineLogger.d(
                                        "requestCameraPermission: ",
                                        "permission not granted"
                                    )
                                    shouldRequestPermission.value = true
                                }
                            }
                        } else {
                            when {
                                ContextCompat.checkSelfPermission(
                                    localContext as Activity,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED -> {
                                    BaselineLogger.d(
                                        "PatImagePreviewScreen",
                                        "Permission previously granted"
                                    )

                                    val imageFile =
                                        baseLineStartViewModel.getFileName(localContext, didi.value)
                                    baseLineStartViewModel.imagePath = imageFile.absolutePath
                                    val uri = uriFromFile(localContext, imageFile)
                                    BaselineLogger.d(
                                        "PatDidiSummaryScreen",
                                        "Retake Photo button Clicked: $uri"
                                    )
                                    baseLineStartViewModel.tempUri = uri
//                                patDidiSummaryViewModel.photoUri = uri
                                    baseLineStartViewModel.saveTempFilePath(
                                        baseLineStartViewModel.imagePath
                                    )
                                    cameraLauncher.launch(uri)
                                    baseLineStartViewModel.shouldShowPhoto.value = false
                                }

                                ActivityCompat.shouldShowRequestPermissionRationale(
                                    localContext as Activity,
                                    Manifest.permission.CAMERA
                                ) -> {
                                    BaselineLogger.d(
                                        "PatImagePreviewScreen",
                                        "Show camera permissions dialog"
                                    )
                                    ActivityCompat.requestPermissions(
                                        localContext as Activity,
                                        arrayOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                        ),
                                        1
                                    )
                                }

                                else -> {
                                    BaselineLogger.d(
                                        "requestCameraPermission: ",
                                        "permission not granted"
                                    )
                                    shouldRequestPermission.value = true
                                }
                            }
                        }
//                                patDidiSummaryViewModel.setCameraExecutor()
//                                patDidiSummaryViewModel.shouldShowCamera.value = true
                    }

                } else {
                    BlueButtonWithIcon(
                        buttonText = stringResource(id = R.string.take_photo_text),
                        icon = Icons.Default.Add,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                    ) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            when {
                                ContextCompat.checkSelfPermission(
                                    localContext as Activity,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                                        && ContextCompat.checkSelfPermission(
                                    localContext as Activity,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) == PackageManager.PERMISSION_GRANTED
                                        && ContextCompat.checkSelfPermission(
                                    localContext as Activity,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ) == PackageManager.PERMISSION_GRANTED -> {
                                    BaselineLogger.d(
                                        "PatImagePreviewScreen",
                                        "Permission previously granted"
                                    )

                                    val imageFile =
                                        baseLineStartViewModel.getFileName(localContext, didi.value)
                                    baseLineStartViewModel.imagePath = imageFile.absolutePath
                                    val uri = uriFromFile(localContext, imageFile)
                                    baseLineStartViewModel.tempUri = uri
                                    baseLineStartViewModel.saveTempFilePath(
                                        baseLineStartViewModel.imagePath
                                    )
                                    cameraLauncher.launch(uri)
                                }

                                ActivityCompat.shouldShowRequestPermissionRationale(
                                    localContext as Activity,
                                    Manifest.permission.CAMERA
                                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                    localContext as Activity,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                                    localContext as Activity,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ) -> {
                                    BaselineLogger.d(
                                        "PatImagePreviewScreen",
                                        "Show camera permissions dialog"
                                    )
                                    ActivityCompat.requestPermissions(
                                        localContext as Activity,
                                        arrayOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                        ),
                                        1
                                    )
                                }

                                else -> {
                                    BaselineLogger.d(
                                        "requestCameraPermission: ",
                                        "permission not granted"
                                    )
                                    shouldRequestPermission.value = true
                                }
                            }
                        } else {
                            when {
                                ContextCompat.checkSelfPermission(
                                    localContext as Activity,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED -> {
                                    BaselineLogger.d(
                                        "PatImagePreviewScreen",
                                        "Permission previously granted"
                                    )

                                    val imageFile =
                                        baseLineStartViewModel.getFileName(localContext, didi.value)
                                    baseLineStartViewModel.imagePath = imageFile.absolutePath
                                    val uri = uriFromFile(localContext, imageFile)
                                    baseLineStartViewModel.tempUri = uri
                                    baseLineStartViewModel.saveTempFilePath(
                                        baseLineStartViewModel.imagePath
                                    )
                                    cameraLauncher.launch(uri)
                                }

                                ActivityCompat.shouldShowRequestPermissionRationale(
                                    localContext as Activity,
                                    Manifest.permission.CAMERA
                                ) -> {
                                    BaselineLogger.d(
                                        "PatImagePreviewScreen",
                                        "Show camera permissions dialog"
                                    )
                                    ActivityCompat.requestPermissions(
                                        localContext as Activity,
                                        arrayOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                        ),
                                        1
                                    )
                                }

                                else -> {
                                    BaselineLogger.d(
                                        "requestCameraPermission: ",
                                        "permission not granted"
                                    )
                                    shouldRequestPermission.value = true
                                }
                            }
                        }
//                                patDidiSummaryViewModel.shouldShowCamera.value = true
                    }
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = it.calculateBottomPadding() + defaultBottomBarPadding)
                )
            }
        }
    }

}

private fun updateDidiDetails(
    didi: StateFlow<SurveyeeEntity>,
    baseLineStartViewModel: BaseLineStartViewModel
) {
    didi.value.didiId?.let {
        baseLineStartViewModel.onEvent(
            SurveyStateEvents.UpdateDidiSurveyStatus(
                it,
                didiInfo = DidiInfoEntity(
                    didiId = it,
                    isAdharCard = baseLineStartViewModel.isAdharCard.value,
                    isVoterCard = baseLineStartViewModel.isVoterCard.value,
                    adharNumber = baseLineStartViewModel.aadharNumber.value,
                    phoneNumber = baseLineStartViewModel.phoneNumber.value
                ),
                SurveyState.INPROGRESS
            )
        )
        baseLineStartViewModel.onEvent(
            EventWriterEvents.UpdateSectionStatusEvent(
                surveyId = baseLineStartViewModel.sectionDetails.surveyId,
                sectionId = baseLineStartViewModel.sectionDetails.sectionId,
                didiId = didi.value.didiId ?: 0,
                sectionStatus = SectionStatus.COMPLETED
            )
        )
    }
}

@Composable
fun TextDetails(title: String, data: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, style = defaultTextStyle, color = textColorDark)
        Text(
            text = data, style = smallTextStyle
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BaseLineStartScreenPreview(
) {
//    BaseLineStartScreen(baseLineStartViewModel = hiltViewModel())

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.photo_camera_icon),
            contentDescription = null,
            tint = textColorDark50
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.no_photo_available_text),
            style = smallTextStyle,
            color = textColorDark50
        )
    }
}

private fun getCasteName(casteId: Int, baseLineStartViewModel: BaseLineStartViewModel): String {
    var casteList = baseLineStartViewModel.getCasteListForSelectedLanguage()

    return casteList.find { it.id == casteId }?.casteName ?: BLANK_STRING
}