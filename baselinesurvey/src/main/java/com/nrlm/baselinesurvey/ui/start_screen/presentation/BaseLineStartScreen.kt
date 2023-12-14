package com.nrlm.baselinesurvey.ui.start_screen.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.activity.MainActivity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.navigation.home.SECTION_SCREEN_ROUTE_NAME
import com.nrlm.baselinesurvey.ui.common_components.BlueButtonWithIcon
import com.nrlm.baselinesurvey.ui.common_components.ButtonOutline
import com.nrlm.baselinesurvey.ui.common_components.DialogComponent
import com.nrlm.baselinesurvey.ui.common_components.DoubleButtonBox
import com.nrlm.baselinesurvey.ui.start_screen.viewmodel.BaseLineStartViewModel
import com.nrlm.baselinesurvey.ui.theme.languageItemActiveBg
import com.nrlm.baselinesurvey.ui.theme.smallTextStyle
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleWithNormalWeight
import com.nrlm.baselinesurvey.ui.theme.textColorDark50
import com.nrlm.baselinesurvey.utils.BaselineLogger
import com.nrlm.baselinesurvey.utils.LocationCoordinates
import com.nrlm.baselinesurvey.utils.LocationUtil
import com.nrlm.baselinesurvey.utils.openSettings
import com.nrlm.baselinesurvey.utils.uriFromFile
import java.util.function.Consumer

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun BaseLineStartScreen(
    navController: NavHostController,
    baseLineStartViewModel: BaseLineStartViewModel,
    didiId: Int,
) {
    val localContext = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val shouldRequestPermission = remember {
        mutableStateOf(false)
    }
    val didi = baseLineStartViewModel.didiEntity
    LaunchedEffect(key1 = true) {
        baseLineStartViewModel.getDidiDetails(didiId)
    }

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
        bottomBar = {
            DoubleButtonBox(
                modifier = Modifier
                    .shadow(10.dp),
                positiveButtonText = stringResource(id = R.string.continue_text),
                negativeButtonText = stringResource(id = R.string.go_back_text),
                positiveButtonOnClick = {
                },
                negativeButtonOnClick = {
                    navController.navigate(SECTION_SCREEN_ROUTE_NAME)
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp)
                .padding(top = it.calculateTopPadding() + 6.dp),
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
            TextDetails(title = "Didi: ", data = didi.value.didiName)
            TextDetails(title = "Dada: ", data = didi.value.dadaName)
            TextDetails(title = "Caste: ", data = didi.value.casteId.toString())
            Spacer(modifier = Modifier.height(10.dp))

            var hasImage by remember {
                mutableStateOf(false)
            }
            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture(),
                onResult = { success ->
                    hasImage = success
                    BaselineLogger.d(
                        "PatDidiSummaryScreen",
                        "rememberLauncherForActivityResult -> onResult = success: $success"
                    )
                    if (success) {
                        baseLineStartViewModel.photoUri = baseLineStartViewModel.tempUri
                        handleImageCapture(
                            uri = baseLineStartViewModel.photoUri,
                            photoPath = baseLineStartViewModel.imagePath,
                            context = (localContext as MainActivity),
                            didi.value,
                            viewModal = baseLineStartViewModel
                        )
                    } else {
                        baseLineStartViewModel.shouldShowPhoto.value =
                            !(baseLineStartViewModel.photoUri == null || baseLineStartViewModel.photoUri == Uri.EMPTY)
                    }
                }
            )
            if (baseLineStartViewModel.shouldShowPhoto.value) {
                AsyncImage(
                    model = baseLineStartViewModel.photoUri,
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
        }

    }
}

@Composable
fun TextDetails(title: String, data: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, style = smallTextStyleWithNormalWeight)
        Text(
            text = data, style = smallTextStyle
        )
    }
}

fun handleImageCapture(
    uri: Uri,
    photoPath: String,
    context: Activity,
    didiEntity: SurveyeeEntity,
    viewModal: BaseLineStartViewModel
) {

    //   NudgeLogger.d("PatDidiSummaryScreen", "handleImageCapture -> called")
//    viewModal.shouldShowCamera.value = false
//    viewModal.photoUri = uri
    viewModal.updatedLocalPath.value = photoPath
    viewModal.shouldShowPhoto.value = true
    var location = LocationCoordinates(0.0, 0.0)

//    val decimalFormat = DecimalFormat("#.#######")
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


    viewModal.saveFilePathInDb(photoPath, location, didiEntity = didiEntity)
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