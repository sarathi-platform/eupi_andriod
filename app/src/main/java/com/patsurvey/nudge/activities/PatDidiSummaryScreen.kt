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
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
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
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.didiDetailItemStyle
import com.patsurvey.nudge.activities.ui.theme.didiDetailLabelStyle
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg
import com.patsurvey.nudge.activities.ui.theme.lightGray2
import com.patsurvey.nudge.activities.ui.theme.red
import com.patsurvey.nudge.activities.ui.theme.smallTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.textColorDark50
import com.patsurvey.nudge.activities.ui.theme.textColorDark80
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.utils.AbleBodiedFlag
import com.patsurvey.nudge.utils.BlueButtonWithIcon
import com.patsurvey.nudge.utils.ButtonOutline
import com.patsurvey.nudge.utils.DoubleButtonBox
import com.patsurvey.nudge.utils.LocationCoordinates
import com.patsurvey.nudge.utils.LocationUtil
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PageFrom
import com.patsurvey.nudge.utils.SHGFlag
import com.patsurvey.nudge.utils.TYPE_EXCLUSION
import com.patsurvey.nudge.utils.openSettings
import com.patsurvey.nudge.utils.updateStepStatus
import com.patsurvey.nudge.utils.uriFromFile
import kotlinx.coroutines.delay
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
    val context = LocalContext.current
    val shgFlag = remember {
        mutableStateOf(-1)
    }

    val ableBodiedFlag = remember {
        mutableStateOf(-1)
    }

    LaunchedEffect(key1 = true) {
        patDidiSummaryViewModel.getDidiDetails(didiId)
        delay(100)
        shgFlag.value = patDidiSummaryViewModel.didiEntity.value.shgFlag
    }

    if (patDidiSummaryViewModel.patDidiSummaryRepository.prefRepo.questionScreenOpenFrom() == PageFrom.NOT_AVAILABLE_STEP_COMPLETE_CAMERA_PAGE.ordinal) {
        BackHandler {
            (context as MainActivity).isBackFromSummary.value = true
            navController.popBackStack()
        }
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


        val (bottomActionBox, mainBox) = createRefs()
        Box(modifier = Modifier
            .constrainAs(mainBox) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }
        ) {
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
                        prefRepo = patDidiSummaryViewModel.patDidiSummaryRepository.prefRepo,
                        modifier = Modifier.fillMaxWidth(),
                        startPadding = 0.dp
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = bottomPadding)
                            .padding(horizontal = 4.dp)
                            .verticalScroll(rememberScrollState()),
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
                                text = patDidiSummaryViewModel.getCastName(didi.value.castId),
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
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    yesNoButtonViewHeight.value =
                                        with(localDensity) { coordinates.size.height.toDp() }
                                }
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
                                        append(stringResource(R.string.able_bodied_women_flag_text))
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
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier/*.width()*/
                            )

                            Spacer(modifier = Modifier.width(4.dp))

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
                                            ableBodiedFlag.value = AbleBodiedFlag.YES.value
                                            //add method to update flag value in db
                                            patDidiSummaryViewModel.updateDidiAbleBodiedFlag(
                                                didiId,
                                                AbleBodiedFlag.YES
                                            )
                                        }, modifier = Modifier
                                            .clip(
                                                RoundedCornerShape(
                                                    topStart = 6.dp,
                                                    bottomStart = 6.dp
                                                )
                                            )
                                            .background(
                                                if (ableBodiedFlag.value == AbleBodiedFlag.YES.value) blueDark else Color.Transparent,
                                                RoundedCornerShape(
                                                    topStart = 6.dp,
                                                    bottomStart = 6.dp
                                                )
                                            )
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.option_yes),
                                            color = if (ableBodiedFlag.value == AbleBodiedFlag.YES.value) white else textColorDark
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
                                            ableBodiedFlag.value = AbleBodiedFlag.NO.value
                                            //add method to update flag value in db
                                            patDidiSummaryViewModel.updateDidiAbleBodiedFlag(
                                                didiId,
                                                AbleBodiedFlag.NO
                                            )
                                        }, modifier = Modifier
                                            .clip(
                                                RoundedCornerShape(
                                                    topEnd = 6.dp,
                                                    bottomEnd = 6.dp
                                                )
                                            )
                                            .background(
                                                if (ableBodiedFlag.value == AbleBodiedFlag.NO.value) blueDark else Color.Transparent,
                                                RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
                                            )
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.option_no),
                                            color = if (ableBodiedFlag.value == AbleBodiedFlag.NO.value) white else textColorDark
                                        )
                                    }
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
                                NudgeLogger.d(
                                    "PatDidiSummaryScreen",
                                    "rememberLauncherForActivityResult -> onResult = success: $success"
                                )
                                if (success) {
                                    patDidiSummaryViewModel.photoUri =
                                        patDidiSummaryViewModel.tempUri
                                    handleImageCapture(
                                        uri = patDidiSummaryViewModel.photoUri,
                                        photoPath = patDidiSummaryViewModel.imagePath,
                                        context = (localContext as MainActivity),
                                        didi.value,
                                        viewModal = patDidiSummaryViewModel
                                    )
                                } else {
                                    patDidiSummaryViewModel.shouldShowPhoto.value =
                                        !(patDidiSummaryViewModel.photoUri == null || patDidiSummaryViewModel.photoUri == Uri.EMPTY)
                                }
                            }
                        )

                        if (patDidiSummaryViewModel.shouldShowPhoto.value) {
                            AsyncImage(
                                model = patDidiSummaryViewModel.photoUri,
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
                                                    NudgeLogger.d(
                                                        "PatImagePreviewScreen",
                                                        "Permission previously granted"
                                                    )

                                                    val imageFile =
                                                        patDidiSummaryViewModel.getFileName(
                                                            localContext,
                                                            didi.value
                                                        )
                                                    patDidiSummaryViewModel.imagePath =
                                                        imageFile.absolutePath
                                                    val uri = uriFromFile(localContext, imageFile)
                                                    patDidiSummaryViewModel.tempUri = uri
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
                                                    NudgeLogger.d(
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
                                                    NudgeLogger.d(
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
                                                    NudgeLogger.d(
                                                        "PatImagePreviewScreen",
                                                        "Permission previously granted"
                                                    )

                                                    val imageFile =
                                                        patDidiSummaryViewModel.getFileName(
                                                            localContext,
                                                            didi.value
                                                        )
                                                    patDidiSummaryViewModel.imagePath =
                                                        imageFile.absolutePath
                                                    val uri = uriFromFile(localContext, imageFile)
                                                    patDidiSummaryViewModel.tempUri = uri
                                                    cameraLauncher.launch(uri)
                                                }

                                                ActivityCompat.shouldShowRequestPermissionRationale(
                                                    localContext as Activity,
                                                    Manifest.permission.CAMERA
                                                ) -> {
                                                    NudgeLogger.d(
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
                                                    NudgeLogger.d(
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

                        if (patDidiSummaryViewModel.shouldShowPhoto.value) {
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
                                            NudgeLogger.d(
                                                "PatImagePreviewScreen",
                                                "Permission previously granted"
                                            )

                                            val imageFile = patDidiSummaryViewModel.getFileName(
                                                localContext,
                                                didi.value
                                            )
                                            patDidiSummaryViewModel.imagePath =
                                                imageFile.absolutePath
                                            val uri = uriFromFile(localContext, imageFile)
                                            NudgeLogger.d(
                                                "PatDidiSummaryScreen",
                                                "Retake Photo button Clicked: $uri"
                                            )
                                            patDidiSummaryViewModel.tempUri = uri
//                                patDidiSummaryViewModel.photoUri = uri
                                            cameraLauncher.launch(uri)
                                            patDidiSummaryViewModel.shouldShowPhoto.value = false
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
                                            NudgeLogger.d(
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
                                            NudgeLogger.d(
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
                                            NudgeLogger.d(
                                                "PatImagePreviewScreen",
                                                "Permission previously granted"
                                            )

                                            val imageFile = patDidiSummaryViewModel.getFileName(
                                                localContext,
                                                didi.value
                                            )
                                            patDidiSummaryViewModel.imagePath =
                                                imageFile.absolutePath
                                            val uri = uriFromFile(localContext, imageFile)
                                            NudgeLogger.d(
                                                "PatDidiSummaryScreen",
                                                "Retake Photo button Clicked: $uri"
                                            )
                                            patDidiSummaryViewModel.tempUri = uri
//                                patDidiSummaryViewModel.photoUri = uri
                                            cameraLauncher.launch(uri)
                                            patDidiSummaryViewModel.shouldShowPhoto.value = false
                                        }

                                        ActivityCompat.shouldShowRequestPermissionRationale(
                                            localContext as Activity,
                                            Manifest.permission.CAMERA
                                        ) -> {
                                            NudgeLogger.d(
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
                                            NudgeLogger.d(
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
                                            NudgeLogger.d(
                                                "PatImagePreviewScreen",
                                                "Permission previously granted"
                                            )

                                            val imageFile = patDidiSummaryViewModel.getFileName(
                                                localContext,
                                                didi.value
                                            )
                                            patDidiSummaryViewModel.imagePath =
                                                imageFile.absolutePath
                                            val uri = uriFromFile(localContext, imageFile)
                                            patDidiSummaryViewModel.tempUri = uri
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
                                            NudgeLogger.d(
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
                                            NudgeLogger.d(
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
                                            NudgeLogger.d(
                                                "PatImagePreviewScreen",
                                                "Permission previously granted"
                                            )

                                            val imageFile = patDidiSummaryViewModel.getFileName(
                                                localContext,
                                                didi.value
                                            )
                                            patDidiSummaryViewModel.imagePath =
                                                imageFile.absolutePath
                                            val uri = uriFromFile(localContext, imageFile)
                                            patDidiSummaryViewModel.tempUri = uri
                                            cameraLauncher.launch(uri)
                                        }

                                        ActivityCompat.shouldShowRequestPermissionRationale(
                                            localContext as Activity,
                                            Manifest.permission.CAMERA
                                        ) -> {
                                            NudgeLogger.d(
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
                                            NudgeLogger.d(
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
                                .height(30.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }

        }

        if (patDidiSummaryViewModel.shouldShowPhoto.value && !patDidiSummaryViewModel.shouldShowCamera.value
            && shgFlag.value != SHGFlag.NOT_MARKED.value && ableBodiedFlag.value != AbleBodiedFlag.NOT_MARKED.value
        ) {
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
                    if (patDidiSummaryViewModel.patDidiSummaryRepository.prefRepo.questionScreenOpenFrom() == PageFrom.NOT_AVAILABLE_STEP_COMPLETE_CAMERA_PAGE.ordinal) {
                        updateStepStatus(
                            stepsListDao = patDidiSummaryViewModel.patDidiSummaryRepository.stepsListDao,
                            prefRepo = patDidiSummaryViewModel.patDidiSummaryRepository.prefRepo,
                            printTag = "PatDidiSummaryViewModel",
                            didiId = patDidiSummaryViewModel.didiEntity.value.id,
                            didiDao = patDidiSummaryViewModel.patDidiSummaryRepository.didiDao
                        )
                    }
                    if ((localContext as MainActivity).isOnline.value) {
                        if (patDidiSummaryViewModel.didiEntity.value.serverId != 0) {
                            val id = patDidiSummaryViewModel.didiEntity.value.serverId
                            patDidiSummaryViewModel.uploadDidiImage(
                                localContext,
                                patDidiSummaryViewModel.updatedLocalPath.value,
                                id,
                                patDidiSummaryViewModel.didiImageLocation.value
                            )
                        } else {
                            patDidiSummaryViewModel.setNeedToPostImage(true)
                        }
                    } else {
                        patDidiSummaryViewModel.setNeedToPostImage(true)
                    }
                    patDidiSummaryViewModel.patDidiSummaryRepository.prefRepo.saveQuestionScreenOpenFrom(
                        PageFrom.DIDI_LIST_PAGE.ordinal
                    )
                    val questionIndex = 0
                    if (patDidiSummaryViewModel.patDidiSummaryRepository.prefRepo.isUserBPC()) {
                        navController.navigate("bpc_yes_no_question_screen/${didi.value.id}/$TYPE_EXCLUSION/$questionIndex")
                    } else {
                        navController.navigate("yes_no_question_screen/${didi.value.id}/${TYPE_EXCLUSION}/$questionIndex")
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

    NudgeLogger.d("PatDidiSummaryScreen", "handleImageCapture -> viewModal.saveFilePathInDb called")


    viewModal.saveFilePathInDb(uri, photoPath, location, didiEntity = didiEntity)

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
