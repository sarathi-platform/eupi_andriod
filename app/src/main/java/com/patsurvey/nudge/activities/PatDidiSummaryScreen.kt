package com.patsurvey.nudge.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.socialmapping.ShowDialog
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.utils.*
import java.text.DecimalFormat
import java.util.function.Consumer


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PatDidiSummaryScreen(
    navController: NavHostController,
    modifier: Modifier,
    isOnline: Boolean = true,
    patDidiSummaryViewModel: PatDidiSummaryViewModel,
    didiId: Int,
    onNavigation: () -> Unit
) {

//    val didi = Gson().fromJson(didiDetails, DidiEntity::class.java)

    LaunchedEffect(key1 = true) {
        patDidiSummaryViewModel.getDidiDetails(didiId)
    }

    val didi = patDidiSummaryViewModel.didiEntity

    val localContext = LocalContext.current

    val localDensity = LocalDensity.current
    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }

    val shouldRequestPermission = remember {
        mutableStateOf(false)
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    )

    LaunchedEffect(key1 = localContext) {
        patDidiSummaryViewModel.setUpOutputDirectory(localContext as MainActivity)
        requestCameraPermission(localContext as Activity, patDidiSummaryViewModel) {
            shouldRequestPermission.value = it
        }
    }

    val yesNoButtonViewHeight = remember {
        mutableStateOf(0.dp)
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(modifier)
    ) {

        val shgFlag = remember {
            mutableStateOf(-1)
        }

        val (bottomActionBox, mainBox) = createRefs()
        Box(modifier = Modifier
            .constrainAs(mainBox) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }
        ) {

            AnimatedVisibility(patDidiSummaryViewModel.shouldShowCamera.value) {
                /*CameraView(
                    modifier = Modifier.fillMaxSize(),
                    outputDirectory = patDidiSummaryViewModel.outputDirectory,
                    viewModel = patDidiSummaryViewModel,
                    didiEntity = didi.value,
                    executor = patDidiSummaryViewModel.cameraExecutor,
                    onImageCaptured = { uri, photoPath ->
                        handleImageCapture(
                            uri = uri,
                            photoPath,
                            context = localContext as Activity,
                            didi.value,
                            patDidiSummaryViewModel
                        )
                    },
                    onCloseButtonClicked = {
                        patDidiSummaryViewModel.shouldShowCamera.value = false
                    },
                    onError = { NudgeLogger.e("PatDidiSummaryScreen", "View error:", it) }
                )*/
            }
            AnimatedVisibility(visible = !patDidiSummaryViewModel.shouldShowCamera.value) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(top = 14.dp)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (shouldRequestPermission.value) {
                        ShowDialog(
                            title = stringResource(R.string.permission_required_prompt_title),
                            message = stringResource(R.string.permission_dialog_prompt_message),
                            setShowDialog = {
                                shouldRequestPermission.value = it
                            }
                        ) {
                            openSettings(localContext)
                        }
                    }

                    VOAndVillageBoxView(
                        prefRepo = patDidiSummaryViewModel.prefRepo,
                        modifier = Modifier.fillMaxWidth(),
                        startPadding = 0.dp
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = bottomPadding),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier
                        ) {
                            MainTitle(
                                stringResource(R.string.pat_survey_title),
                                Modifier.weight(1f)
                            )
                        }
                        Row() {

                            Text(
                                text = stringResource(id = R.string.house_number) + ": ",
                                style = didiDetailLabelStyle,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                            )
                            Text(
                                text = didi.value.address,
                                style = didiDetailItemStyle,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                            )
                        }
                        Row() {


                            Text(
                                text = stringResource(id = R.string.didi) + ": ",
                                style = didiDetailLabelStyle,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                            )

                            Text(
                                text = didi.value.name,
                                style = didiDetailItemStyle,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                            )
                        }
                        Row {
                            Text(
                                text = stringResource(id = R.string.dada) + ": ",
                                style = didiDetailLabelStyle,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                            )

                            Text(
                                text = didi.value.guardianName,
                                style = didiDetailItemStyle,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                            )
                        }
                        Row() {
                            Text(
                                text = stringResource(id = R.string.tola) + ": ",
                                style = didiDetailLabelStyle,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                            )

                            Text(
                                text = didi.value.cohortName,
                                style = didiDetailItemStyle,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                            )
                        }
                        Row() {
                            Text(
                                text = stringResource(id = R.string.caste) + ": ",
                                style = didiDetailLabelStyle,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                            )

                            Text(
                                text = didi.value.castName ?: BLANK_STRING,
                                style = didiDetailItemStyle,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontFamily = NotoSans,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 14.sp,
                                            color = textColorDark80
                                        )
                                    ) {
                                        append(stringResource(R.string.shg_question_text))
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            color = red,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = NotoSans
                                        )
                                    ) {
                                        append(" *")
                                    }
                                },

                                )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(
                                        1.dp,
                                        color = lightGray2,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .background(white, shape = RoundedCornerShape(6.dp))
                                    .padding(0.dp)
                            ) {
//                                shgFlag.value = didi.value.shgFlag
                                Row(
                                    Modifier
                                        .padding(0.dp)
                                        .onGloballyPositioned { coordinates ->
                                            yesNoButtonViewHeight.value =
                                                with(localDensity) { coordinates.size.height.toDp() }

                                        }
                                ) {
                                    TextButton(
                                        onClick = {
                                            shgFlag.value = SHGFlag.YES.value
                                            patDidiSummaryViewModel.updateDidiShgFlag(
                                                didiId,
                                                SHGFlag.YES
                                            )
                                        }, modifier = Modifier
                                            .clip(
                                                RoundedCornerShape(
                                                    topStart = 6.dp,
                                                    bottomStart = 6.dp
                                                )
                                            )
                                            .background(
                                                if (shgFlag.value == SHGFlag.YES.value) blueDark else Color.Transparent,
                                                RoundedCornerShape(
                                                    topStart = 6.dp,
                                                    bottomStart = 6.dp
                                                )
                                            )
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.option_yes),
                                            color = if (shgFlag.value == SHGFlag.YES.value) white else textColorDark
                                        )
                                    }
                                    Divider(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(yesNoButtonViewHeight.value)
                                            .background(lightGray2)
                                    )
                                    TextButton(
                                        onClick = {
                                            shgFlag.value = SHGFlag.NO.value
                                            patDidiSummaryViewModel.updateDidiShgFlag(
                                                didiId,
                                                SHGFlag.NO
                                            )
                                        }, modifier = Modifier
                                            .clip(
                                                RoundedCornerShape(
                                                    topEnd = 6.dp,
                                                    bottomEnd = 6.dp
                                                )
                                            )
                                            .background(
                                                if (shgFlag.value == SHGFlag.NO.value) blueDark else Color.Transparent,
                                                RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
                                            )
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.option_no),
                                            color = if (shgFlag.value == SHGFlag.NO.value) white else textColorDark
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        if (patDidiSummaryViewModel.shouldShowPhoto.value) {
                            AsyncImage(
                                model = patDidiSummaryViewModel.photoUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .height((screenHeight/2).dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        languageItemActiveBg,
                                        shape = RoundedCornerShape(6.dp)
                                    ),
                                contentScale = ContentScale.FillBounds
                            )
                            /*Image(
                                painter = rememberImagePainter(patDidiSummaryViewModel.photoUri),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(200.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        languageItemActiveBg,
                                        shape = RoundedCornerShape(6.dp)
                                    ),
                                contentScale = ContentScale.FillWidth
                            )*/
                        } else {
                            Box(
                                modifier = Modifier
                                    .height((screenHeight/2).dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        languageItemActiveBg,
                                        shape = RoundedCornerShape(6.dp)
                                    )
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

                        var hasImage by remember {
                            mutableStateOf(false)
                        }

                        val cameraLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.TakePicture(),
                            onResult = { success ->
                                hasImage = success
                                NudgeLogger.d("PatDidiSummaryScreen", "rememberLauncherForActivityResult -> onResult = success: $success")
                                handleImageCapture(uri = patDidiSummaryViewModel.photoUri, photoPath = patDidiSummaryViewModel.imagePath, context = (localContext as MainActivity), didi.value, viewModal = patDidiSummaryViewModel)
                            }
                        )

                        if (patDidiSummaryViewModel.shouldShowPhoto.value) {
                            ButtonOutline(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(45.dp),
                                buttonTitle = stringResource(id = R.string.retake_photo_text)
                            ) {
                                val imageFile = patDidiSummaryViewModel.getFileName(localContext, didi.value)
                                patDidiSummaryViewModel.imagePath = imageFile.absolutePath
                                val uri = uriFromFile(localContext, imageFile)
                                NudgeLogger.d("PatDidiSummaryScreen", "Retake Photo button Clicked: $uri")
                                patDidiSummaryViewModel.photoUri = uri
                                cameraLauncher.launch(uri)
                                patDidiSummaryViewModel.shouldShowPhoto.value = false

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
                                val imageFile = patDidiSummaryViewModel.getFileName(localContext, didi.value)
                                patDidiSummaryViewModel.imagePath = imageFile.absolutePath
                                val uri = uriFromFile(localContext, imageFile)
                                patDidiSummaryViewModel.photoUri = uri
                                cameraLauncher.launch(uri)
//                                patDidiSummaryViewModel.shouldShowCamera.value = true
                            }
                        }
                        Spacer(modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth())
                    }
                }
            }

        }

        if (patDidiSummaryViewModel.shouldShowPhoto.value && !patDidiSummaryViewModel.shouldShowCamera.value && shgFlag.value != SHGFlag.NOT_MARKED.value) {
            DoubleButtonBox(
                modifier = Modifier
                    .shadow(10.dp)
                    .constrainAs(bottomActionBox) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                    .onGloballyPositioned { coordinates ->
                        bottomPadding = with(localDensity) {
                            coordinates.size.height.toDp()
                        }
                    },
                negativeButtonRequired = false,
                positiveButtonText = stringResource(id = R.string.next),
                positiveButtonOnClick = {
                    if((localContext as MainActivity).isOnline.value) {
                        val id = if (patDidiSummaryViewModel.didiEntity.value.serverId == 0
                        ) patDidiSummaryViewModel.didiEntity.value.id else patDidiSummaryViewModel.didiEntity.value.serverId
                        patDidiSummaryViewModel.uploadDidiImage(
                            localContext,
                            patDidiSummaryViewModel.updatedLocalPath.value,
                            id,
                            patDidiSummaryViewModel.didiImageLocation.value
                        )
                    }
                    if (patDidiSummaryViewModel.prefRepo.isUserBPC()){
                        navController.navigate("bpc_yes_no_question_screen/${didi.value.id}/$TYPE_EXCLUSION")
                    } else {
                        navController.navigate("yes_no_question_screen/${didi.value.id}/${TYPE_EXCLUSION}")
                    }
                },
                negativeButtonOnClick = {}
            )

        }
    }
}

fun handleImageCapture(
    uri: Uri,
    photoPath: String,
    context: Activity,
    didiEntity: DidiEntity,
    viewModal: PatDidiSummaryViewModel
) {

    NudgeLogger.d("PatDidiSummaryScreen", "handleImageCapture -> called")
//    viewModal.shouldShowCamera.value = false
//    viewModal.photoUri = uri
    viewModal.updatedLocalPath.value = photoPath
    viewModal.shouldShowPhoto.value = true
    viewModal.cameraExecutor.shutdown()

    var location = LocationCoordinates(0.0,0.0)

    val decimalFormat = DecimalFormat("#.#######")
    if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        var locationByGps: Location? = null
        var locationByNetwork: Location? = null
        val gpsConsumer = Consumer<Location> { gpsLocation ->
            if (gpsLocation != null) {
                locationByGps = gpsLocation
                location = LocationCoordinates(
                    decimalFormat.format(locationByGps?.latitude ?: 0.0).toDouble(),
                    decimalFormat.format(locationByGps?.longitude ?: 0.0).toDouble()

                )
            }
        }
        val networkConsumer = Consumer<Location> { networkLocation ->
            if (networkLocation != null) {
                locationByNetwork = networkLocation
                location = LocationCoordinates(
                    decimalFormat.format(locationByNetwork?.latitude ?: 0.0).toDouble(),
                    decimalFormat.format(locationByNetwork?.longitude ?: 0.0).toDouble()

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
                    decimalFormat.format(locationByGps?.latitude ?: 0.0).toDouble(),
                    decimalFormat.format(locationByGps?.longitude ?: 0.0).toDouble()

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
                    decimalFormat.format(locationByNetwork?.latitude ?: 0.0).toDouble(),
                    decimalFormat.format(locationByNetwork?.longitude ?: 0.0).toDouble()

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

    NudgeLogger.d("PatDidiSummaryScreen", "handleImageCapture -> viewModal.saveFilePathInDb called")


    viewModal.saveFilePathInDb(photoPath, location, didiEntity = didiEntity)
}

private fun requestCameraPermission(
    context: Activity,
    viewModal: PatDidiSummaryViewModel,
    requestPermission: (Boolean) -> Unit
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("PatImagePreviewScreen", "Permission previously granted")
                requestPermission(false)
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                Manifest.permission.CAMERA
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                viewModal.shouldShowCamera.value = false
                Log.i("PatImagePreviewScreen", "Show camera permissions dialog")
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    1
                )
            }

            else -> {
                viewModal.shouldShowCamera.value = false
                Log.d("requestCameraPermission: ", "permission not granted")
                requestPermission(true)
            }
        }
    } else {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("PatImagePreviewScreen", "Permission previously granted")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                Manifest.permission.CAMERA
            ) -> {
                viewModal.shouldShowCamera.value = false
                Log.i("PatImagePreviewScreen", "Show camera permissions dialog")
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    1
                )
            }

            else -> {
                viewModal.shouldShowCamera.value = false
                Log.d("requestCameraPermission: ", "permission not granted")
                requestPermission(true)
            }
        }
    }
}

/*
private fun patDidiDetailConstraints(): ConstraintSet {
    return ConstraintSet {
        val divider = createRefFor("divider")
        val houseNumberLabel = createRefFor("houseNumberLabel")
        val houseNumber = createRefFor("houseNumber")
        val dadaNameLabel = createRefFor("dadaNameLabel")
        val dadaName = createRefFor("dadaName")
        val casteLabel = createRefFor("casteLabel")
        val caste = createRefFor("caste")
        val tolaLabel = createRefFor("tolaLabel")
        val tola = createRefFor("tola")
        val latestStatusLabel = createRefFor("latestStatusLabel")
        val latestStatus = createRefFor("latestStatus")
        val bottomPadding = createRefFor("bottomPadding")

//        val centerGuideline = createGuidelineFromStart(0.5f)


        constrain(divider) {
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            start.linkTo(parent.start)
        }

        constrain(houseNumberLabel) {
            start.linkTo(parent.start, margin = 15.dp)
            top.linkTo(divider.bottom, margin = 15.dp)
//            end.linkTo(centerGuideline)
//            width = Dimension.fillToConstraints
        }

        constrain(houseNumber) {
            start.linkTo(houseNumberLabel.end, margin = 10.dp)
            top.linkTo(houseNumberLabel.top)
            bottom.linkTo(houseNumberLabel.bottom)
//            end.linkTo(parent.end, margin = 10.dp)
//            width = Dimension.fillToConstraints
        }

        constrain(latestStatusLabel) {
            start.linkTo(houseNumberLabel.start)
            top.linkTo(houseNumberLabel.bottom, margin = 20.dp)
//            end.linkTo(centerGuideline)
//            width = Dimension.fillToConstraints
        }

        constrain(dadaName) {
            start.linkTo(dadaNameLabel.end, margin = 10.dp)
            top.linkTo(latestStatusLabel.top)
            bottom.linkTo(latestStatusLabel.bottom)
//            end.linkTo(parent.end, margin = 10.dp)
//            width = Dimension.fillToConstraints
        }
        constrain(casteLabel) {
            start.linkTo(houseNumberLabel.start)
            top.linkTo(tolaLabel.bottom, margin = 15.dp)
//            end.linkTo(centerGuideline)
//            width = Dimension.fillToConstraints
        }

        constrain(latestStatus) {
            start.linkTo(latestStatusLabel.end, margin = 10.dp)
            top.linkTo(casteLabel.top)
            bottom.linkTo(casteLabel.bottom)
//            end.linkTo(parent.end, margin = 10.dp)
//            width = Dimension.fillToConstraints
        }
        constrain(dadaNameLabel) {
            start.linkTo(houseNumberLabel.start)
            top.linkTo(latestStatusLabel.bottom, margin = 20.dp)
//            end.linkTo(centerGuideline)
//            width = Dimension.fillToConstraints
        }

        constrain(caste) {
            start.linkTo(casteLabel.end, margin = 10.dp)
            top.linkTo(dadaNameLabel.top)
            bottom.linkTo(dadaNameLabel.bottom)
//            end.linkTo(parent.end, margin = 10.dp)
//            width = Dimension.fillToConstraints
        }
        constrain(tolaLabel) {
            start.linkTo(houseNumberLabel.start)
            top.linkTo(dadaNameLabel.bottom, margin = 15.dp)
//            end.linkTo(centerGuideline)
//            width = Dimension.fillToConstraints
        }

        constrain(tola) {
            start.linkTo(tolaLabel.end, margin = 10.dp)
            top.linkTo(tolaLabel.top)
            bottom.linkTo(tolaLabel.bottom)
//            end.linkTo(parent.end, margin = 10.dp)
//            width = Dimension.fillToConstraints
        }

        constrain(bottomPadding) {
            start.linkTo(parent.start)
            top.linkTo(latestStatus.bottom)
        }
    }
}*/
