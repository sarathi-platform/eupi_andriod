package com.patsurvey.nudge.activities.ui.vo_endorsement

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.CameraViewForForm
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.ui.theme.mediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.utils.DoubleButtonBox
import com.patsurvey.nudge.utils.FORM_C
import com.patsurvey.nudge.utils.FORM_D
import com.patsurvey.nudge.utils.OutlineButtonWithIcon

@Composable
fun FormPictureScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    formPictureScreenViewModel: FormPictureScreenViewModel
) {

    val localContext = LocalContext.current

    val screenHeight = LocalConfiguration.current.screenHeightDp

    LaunchedEffect(key1 = localContext) {
        formPictureScreenViewModel.setUpOutputDirectory(localContext as MainActivity)
        requestCameraPermission(localContext as Activity, formPictureScreenViewModel)
    }

    val localDensity = LocalDensity.current
    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }

    BackHandler() {
        navController.popBackStack()
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
            .padding(horizontal = 16.dp)
        ) {

            AnimatedVisibility(formPictureScreenViewModel.shouldShowCamera.value.second) {
                CameraViewForForm(
                    modifier = Modifier.fillMaxSize(),
                    outputDirectory = formPictureScreenViewModel.outputDirectory,
                    formName = formPictureScreenViewModel.shouldShowCamera.value.first,
                    executor = formPictureScreenViewModel.cameraExecutor,
                    onImageCaptured = { uri, photoPath ->
                        handleImageCapture(
                            uri = uri,
                            photoPath,
                            context = localContext as Activity,
                            formName = formPictureScreenViewModel.shouldShowCamera.value.first,
                            formPictureScreenViewModel
                        )
                    },
                    onCloseButtonClicked = {
                        formPictureScreenViewModel.shouldShowCamera.value = Pair("", false)
                    },
                    onError = { Log.e("FormPictureScreen", "View error:", it) }
                )
            }
            AnimatedVisibility(visible = !formPictureScreenViewModel.shouldShowCamera.value.second) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(top = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    VOAndVillageBoxView(
                        prefRepo = formPictureScreenViewModel.prefRepo,
                        modifier = Modifier.fillMaxWidth(),
                        startPadding = 0.dp
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = (screenHeight / 6).dp, horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {

                        Text(
                            text = stringResource(R.string.form_picture_screen_title),
                            style = mediumTextStyle,
                            color = textColorDark
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlineButtonWithIcon(
                            buttonTitle = if (formPictureScreenViewModel.formsClicked.value < 1) stringResource(R.string.form_c_photo_button_text) else stringResource(id = R.string.view),
                            icon = Icons.Default.Add,
                            contentColor = textColorDark,
                            borderColor = textColorDark,
                            showIcon = formPictureScreenViewModel.formsClicked.value < 1,
                            modifier = Modifier
                        ) {
                            if (formPictureScreenViewModel.formsClicked.value < 1) {
                                formPictureScreenViewModel.setCameraExecutor()
                                formPictureScreenViewModel.shouldShowCamera.value =
                                    Pair(FORM_C, true)
                            } else {
                                navController.navigate("image_viewer/$FORM_C")
                            }
                        }

                        OutlineButtonWithIcon(
                            buttonTitle = if (formPictureScreenViewModel.formsClicked.value < 2) stringResource(R.string.form_d_photo_button_text) else stringResource(id = R.string.view),
                            icon = Icons.Default.Add,
                            contentColor = textColorDark,
                            borderColor = textColorDark,
                            showIcon = formPictureScreenViewModel.formsClicked.value < 2,
                            modifier = Modifier
                        ) {
                            if (formPictureScreenViewModel.formsClicked.value < 2) {
                                formPictureScreenViewModel.setCameraExecutor()
                                formPictureScreenViewModel.shouldShowCamera.value =
                                    Pair(FORM_D, true)
                            } else {
                                navController.navigate("image_viewer/$FORM_D")
                            }
                        }
                    }
                }
            }
        }

        if (!formPictureScreenViewModel.shouldShowCamera.value.second && formPictureScreenViewModel.formsClicked.value == 2){
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
                positiveButtonText = stringResource(id = R.string.submit),
                positiveButtonOnClick = {
                    navController.navigate(
                        "vo_endorsement_step_completion_screen/${
                            localContext.getString(R.string.vo_endorsement_completed_message)
                                .replace(
                                    "{VILLAGE_NAME}",
                                    formPictureScreenViewModel.prefRepo.getSelectedVillage().name
                                )
                        }"
                    )
                },
                negativeButtonOnClick = {}
            )
        }
    }
}

private fun handleImageCapture(
    uri: Uri,
    photoPath: String,
    context: Activity,
    formName: String,
    viewModal: FormPictureScreenViewModel
) {
    viewModal.shouldShowCamera.value = Pair("", false)
    viewModal.photoUri = uri
    viewModal.shouldShowPhoto.value = true
    viewModal.cameraExecutor.shutdown()
    viewModal.saveFormPath(photoPath, formName)
    if (viewModal.formsClicked.value < 2) {
        viewModal.formsClicked.value = viewModal.formsClicked.value + 1
    }
}

private fun requestCameraPermission(context: Activity, viewModal: FormPictureScreenViewModel) {
    when {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED -> {
            Log.i("FormPictureScreen", "Permission previously granted")
//            viewModal.shouldShowCamera.value = true
        }
        ActivityCompat.shouldShowRequestPermissionRationale(
            context,
            Manifest.permission.CAMERA
        ) -> {
            Log.i("FormPictureScreen", "Show camera permissions dialog")
//            viewModal.shouldShowCamera.value = true
        }

        else -> viewModal.shouldShowCamera.value = Pair("", false)
    }
}