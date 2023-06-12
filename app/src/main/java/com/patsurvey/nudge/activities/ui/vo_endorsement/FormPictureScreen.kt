package com.patsurvey.nudge.activities.ui.vo_endorsement

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.CameraViewForForm
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FormPictureScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    formPictureScreenViewModel: FormPictureScreenViewModel,
    stepId: Int
) {

    val localContext = LocalContext.current

    val screenHeight = LocalConfiguration.current.screenHeightDp

    LaunchedEffect(key1 = localContext) {
        formPictureScreenViewModel.setUpOutputDirectory(localContext as MainActivity)
        requestCameraPermission(localContext as Activity, formPictureScreenViewModel) {

        }
    }

    val localDensity = LocalDensity.current
    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }

    /*  val showIcon = remember {
          mutableStateOf(false)
      }*/

    val formCCardExpanded = remember {
        mutableStateOf(false)
    }
    val formDCardExpanded = remember {
        mutableStateOf(false)
    }


    BackHandler() {
        navController.popBackStack()
    }

    val scaffoldState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val scale = remember { mutableStateOf(1f) }

    Surface {
        ModalBottomSheetLayout(
            sheetContent = {
                Box {
                    Image(
                        modifier = Modifier
                            .background(Color.Black)
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .pointerInput(Unit) {
                                detectTransformGestures { centroid, pan, zoom, rotation ->
                                    scale.value *= zoom
                                }
                            }
                            .graphicsLayer(
                                // adding some zoom limits (min 100%, max 200%)
                                scaleX = maxOf(0.5f, minOf(3f, scale.value)),
                                scaleY = maxOf(0.5f, minOf(3f, scale.value)),
                            )
                            .then(modifier),
                        contentScale = ContentScale.FillWidth,
                        painter = rememberImagePainter(formPictureScreenViewModel.uri.value),
                        contentDescription = "image"
                    )
                    IconButton(
                        onClick = {
                            scope.launch {
                                scaffoldState.hide()
                            }
                        },
                        Modifier.background(black20)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down_24),
                            contentDescription = "back button",
                            modifier
                                .align(
                                    Alignment.TopEnd
                                )
                                .padding(10.dp),
                            tint = Color.White
                        )
                    }
                }
            },
            sheetState = scaffoldState,
            sheetElevation = 20.dp,
            sheetBackgroundColor = Color.Black,
            sheetShape = RectangleShape
        ) {

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
                    .padding(horizontal = 16.dp)
                    .padding(bottom = bottomPadding)
                ) {

                    AnimatedVisibility(formPictureScreenViewModel.shouldShowCamera.value.second) {
                        CameraViewForForm(
                            modifier = Modifier.fillMaxSize(),
                            outputDirectory = formPictureScreenViewModel.outputDirectory,
                            formName = if (formPictureScreenViewModel.retakeImageIndex.value != -1)
                                formPictureScreenViewModel.getFormSubPath(
                                    formPictureScreenViewModel.shouldShowCamera.value.first,
                                    formPictureScreenViewModel.retakeImageIndex.value + 1
                                )
//                                "${formPictureScreenViewModel.shouldShowCamera.value.first}_page_${formPictureScreenViewModel.retakeImageIndex.value + 1}"
                            else
                                formPictureScreenViewModel.getFormSubPath(
                                    formPictureScreenViewModel.shouldShowCamera.value.first,
                                    formPictureScreenViewModel.formCPageList.value.size + 1
                                )
                            /*"${formPictureScreenViewModel.shouldShowCamera.value.first}_page_${formPictureScreenViewModel.formCPageList.value.size + 1}"*/,
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
                                    .verticalScroll(rememberScrollState())
                                    .padding(vertical = 14.dp, horizontal = 16.dp),
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

                                FormPictureCard(
                                    modifier = Modifier,
                                    navController = navController,
                                    showIcon = formPictureScreenViewModel.formsClicked.value < 1,
                                    cardTitle = if (formPictureScreenViewModel.formsClicked.value < 1) stringResource(
                                        R.string.form_c_photo_button_text
                                    ) else "${stringResource(id = R.string.view)} C",
                                    contentColor = textColorDark,
                                    borderColor = textColorDark,
                                    expanded = formCCardExpanded.value,
                                    pageList = formPictureScreenViewModel.formCPageList.value,
                                    pageItemClicked = {
                                        scope.launch {
                                            formPictureScreenViewModel.pageItemClicked.value =
                                                formPictureScreenViewModel.getFormSubPath(
                                                    FORM_C,
                                                    it
                                                )
                                            formPictureScreenViewModel.imagePath.value =
                                                formPictureScreenViewModel.prefRepo.getPref(
                                                    formPictureScreenViewModel.getFormPathKey(
                                                        formPictureScreenViewModel.pageItemClicked.value
                                                    ),
                                                    ""
                                                )?.let { if (it.isNotEmpty()) it else "" }
                                                    .toString()
                                            if (!formPictureScreenViewModel.imagePath.value.isNullOrEmpty())
                                                formPictureScreenViewModel.setUri(localContext)
                                            delay(250)
                                            if (!scaffoldState.isVisible)
                                                scaffoldState.show()
                                            else
                                                scaffoldState.hide()
                                        }
                                    },
                                    formPictureCardClicked = {
                                        formCCardExpanded.value = !formCCardExpanded.value

                                    },
                                    addPageClicked = {
                                        if (formPictureScreenViewModel.formCPageList.value.size < 5) {
                                            formPictureScreenViewModel.setCameraExecutor()
                                            formPictureScreenViewModel.shouldShowCamera.value =
                                                Pair(FORM_C, true)
                                        } else {
                                            showToast(
                                                localContext,
                                                "Max 5 Pages can be captured"
                                            )
//                                    navController.navigate("image_viewer/$FORM_C")
                                        }
                                    },
                                    retakeButtonClicked = { index ->
                                        formPictureScreenViewModel.retakeImageIndex.value =
                                            index
                                        formPictureScreenViewModel.setCameraExecutor()
                                        formPictureScreenViewModel.shouldShowCamera.value =
                                            Pair(FORM_C, true)
                                        val imageToBeReplaced =
                                            formPictureScreenViewModel.formCImageList.value["Page_${index + 1}"]
                                    },
                                    deleteButtonClicked = {
                                        formPictureScreenViewModel.formCPageList.value =
                                            mutableListOf()
                                        if (formPictureScreenViewModel.formsClicked.value > 1)
                                            formPictureScreenViewModel.formsClicked.value = 1

                                    }
                                )


                                FormPictureCard(
                                    modifier = Modifier,
                                    navController = navController,
                                    showIcon = formPictureScreenViewModel.formsClicked.value < 2,
                                    cardTitle = if (formPictureScreenViewModel.formsClicked.value < 2) stringResource(
                                        R.string.form_d_photo_button_text
                                    ) else "${stringResource(id = R.string.view)} D",
                                    contentColor = textColorDark,
                                    borderColor = textColorDark,
                                    expanded = formDCardExpanded.value,
                                    pageList = formPictureScreenViewModel.formDPageList.value,
                                    pageItemClicked = {
                                        scope.launch {
                                            formPictureScreenViewModel.pageItemClicked.value =
                                                formPictureScreenViewModel.getFormSubPath(
                                                    FORM_D,
                                                    it
                                                )
                                            formPictureScreenViewModel.imagePath.value =
                                                formPictureScreenViewModel.prefRepo.getPref(
                                                    formPictureScreenViewModel.getFormPathKey(
                                                        formPictureScreenViewModel.pageItemClicked.value
                                                    ),
                                                    ""
                                                )?.let { if (it.isNotEmpty()) it else "" }
                                                    .toString()
                                            formPictureScreenViewModel.setUri(localContext)
                                            if (!scaffoldState.isVisible)
                                                scaffoldState.show()
                                            else
                                                scaffoldState.hide()
                                        }
                                    },
                                    formPictureCardClicked = {
                                        formDCardExpanded.value = !formDCardExpanded.value
                                    },
                                    addPageClicked = {
                                        if (formPictureScreenViewModel.formDPageList.value.size < 5) {
                                            formPictureScreenViewModel.setCameraExecutor()
                                            formPictureScreenViewModel.shouldShowCamera.value =
                                                Pair(FORM_D, true)
                                        } else {
                                            showToast(
                                                localContext,
                                                "Max 5 Pages can be captured"
                                            )
                                            //                                    navController.navigate("image_viewer/$FORM_C")
                                        }
                                    },
                                    retakeButtonClicked = { index ->
                                        formPictureScreenViewModel.retakeImageIndex.value =
                                            index
                                        formPictureScreenViewModel.setCameraExecutor()
                                        formPictureScreenViewModel.shouldShowCamera.value =
                                            Pair(FORM_D, true)
                                    },
                                    deleteButtonClicked = {
                                        formPictureScreenViewModel.formDPageList.value =
                                            mutableListOf()
                                        if (formPictureScreenViewModel.formsClicked.value > 1)
                                            formPictureScreenViewModel.formsClicked.value = 1
                                    }
                                )
                            }
                        }
                    }
                }

                if (!formPictureScreenViewModel.shouldShowCamera.value.second && formPictureScreenViewModel.formsClicked.value == 2) {
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
                            if ((context as MainActivity).isOnline.value ?: false) {
                                formPictureScreenViewModel.updateVoStatusToNetwork(object :
                                    NetworkCallbackListener {
                                    override fun onSuccess() {

                                    }

                                    override fun onFailed() {
                                        showCustomToast(context, SYNC_FAILED)
                                    }

                                })
                                formPictureScreenViewModel.callWorkFlowAPI(
                                    formPictureScreenViewModel.prefRepo.getSelectedVillage().id,
                                    stepId,
                                    object :
                                        NetworkCallbackListener {
                                        override fun onSuccess() {
                                        }

                                        override fun onFailed() {
                                            showCustomToast(context, SYNC_FAILED)
                                        }
                                    })
                            }
                            formPictureScreenViewModel.updateDidiVoEndorsementStatus()
                            formPictureScreenViewModel.markVoEndorsementComplete(
                                formPictureScreenViewModel.prefRepo.getSelectedVillage().id,
                                stepId
                            )
                            formPictureScreenViewModel.saveVoEndorsementDate()
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
                } else {
                    bottomPadding = 0.dp
                }
            }
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
    viewModal.photoUri = uri

    viewModal.cameraExecutor.shutdown()

    if (viewModal.shouldShowCamera.value.first == FORM_C) {
        if (viewModal.retakeImageIndex.value != -1) {
            Log.d("FormPictureScreen_handleImageCapture", "photoPath: ${photoPath}")
            viewModal.saveFormPath(
                photoPath,
                viewModal.getFormSubPath(FORM_C, viewModal.retakeImageIndex.value + 1)
                /*"${formName}_page_${viewModal.retakeImageIndex.value + 1}"*/
            )
            viewModal.formCImageList.value = viewModal.formCImageList.value.also {
                it["Page_${viewModal.retakeImageIndex.value}"] = photoPath
            }
            viewModal.retakeImageIndex.value = -1
        } else {
            viewModal.saveFormPath(
                photoPath,
                viewModal.getFormSubPath(FORM_C, viewModal.formCPageList.value.size + 1)
                /*"${formName}_page_${viewModal.formCPageList.value.size + 1}"*/
            )
            viewModal.formCPageList.value.add((viewModal.formCPageList.value.size) + 1)
            viewModal.formCImageList.value = viewModal.formCImageList.value.also {
                it["Page_${viewModal.formCPageList.value.size}"] = photoPath
            }
            if (viewModal.formsClicked.value < 1) {
                viewModal.formsClicked.value = viewModal.formsClicked.value + 1
            }
        }
    } else {
        if (viewModal.retakeImageIndex.value != -1) {
            Log.d("FormPictureScreen_handleImageCapture", "photoPath: ${photoPath}")
            viewModal.saveFormPath(
                photoPath,
                viewModal.getFormSubPath(FORM_D, viewModal.retakeImageIndex.value + 1)
            )
            viewModal.formDImageList.value = viewModal.formDImageList.value.also {
                it["Page_${viewModal.retakeImageIndex.value}"] = photoPath
            }
            viewModal.retakeImageIndex.value = -1
        } else {
            viewModal.saveFormPath(
                photoPath,
                viewModal.getFormSubPath(FORM_D, viewModal.formDPageList.value.size + 1)
            )
            viewModal.formDPageList.value.add((viewModal.formDPageList.value.size) + 1)
            viewModal.formDImageList.value = viewModal.formDImageList.value.also {
                it["Page_${viewModal.formDPageList.value.size}"] = photoPath
            }
            if (viewModal.formsClicked.value < 2) {
                viewModal.formsClicked.value = viewModal.formsClicked.value + 1
            }
        }
    }
    viewModal.shouldShowCamera.value = Pair("", false)
}

private fun requestCameraPermission(context: Activity, viewModal: FormPictureScreenViewModel, requestPermission: () -> Unit) {
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

        else -> {
            viewModal.shouldShowCamera.value = Pair("", false)
            Log.d("requestCameraPermission: ", "permission not granted")
            requestPermission()
        }
    }
}

@Composable
fun FormPictureCard(
    modifier: Modifier = Modifier,
    navController: NavController,
    showIcon: Boolean,
    cardTitle: String,
    contentColor: Color,
    borderColor: Color,
    expanded: Boolean,
    icon: ImageVector = Icons.Default.Add,
    pageList: MutableList<Int>,
    pageItemClicked: (pageNumber: Int) -> Unit,
    formPictureCardClicked: () -> Unit,
    addPageClicked: () -> Unit,
    retakeButtonClicked: (index: Int) -> Unit,
    deleteButtonClicked: () -> Unit
) {

    val transition = updateTransition(expanded, label = "transition")

    val animateInt by transition.animateInt({
        tween(durationMillis = 10)
    }, label = "animate float") {
        if (it) 1 else 0
    }

    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "rotationDegreeTransition") {
        if (it) 0f else -90f
    }

    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(6.dp)
            )
            .fillMaxWidth()
            .clickable {
                formPictureCardClicked()
            }
    ) {

        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .height(45.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (showIcon) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Add Button",
                        tint = contentColor,
                        modifier = Modifier.absolutePadding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = cardTitle,
                    color = contentColor,
                    style = smallTextStyleMediumWeight,
                )
            }

            ExpandableFormPictureCard(
                modifier = Modifier.padding(horizontal = 2.dp, vertical = 8.dp),
                expanded = animateInt == 1,
                navController = navController,
                pageList = pageList,
                addPageClicked = {
                    addPageClicked()
                },
                retakeButtonClicked = {
                    retakeButtonClicked(it)
                },
                pageItemClicked = {
                    pageItemClicked(it)
                },
                deleteButtonClicked = {
                    deleteButtonClicked()
                }
            )
        }
    }
}


@Composable
fun ExpandableFormPictureCard(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    pageList: MutableList<Int>,
    navController: NavController,
    addPageClicked: () -> Unit,
    pageItemClicked: (pageNumber: Int) -> Unit,
    retakeButtonClicked: (index: Int) -> Unit,
    deleteButtonClicked: () -> Unit
) {
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        )
    }
    val exitTransition = remember {
        shrinkVertically(
            // Expand from the top.
            shrinkTowards = Alignment.Top,
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        ) + fadeOut(
            // Fade in with the initial alpha of 0.3f.
            animationSpec = tween(EXPANSTION_TRANSITION_DURATION)
        )
    }

    AnimatedVisibility(
        visible = expanded,
        enter = enterTransition,
        exit = exitTransition,
        modifier = Modifier.then(modifier)
    ) {

        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.scrollable(
                rememberScrollState(),
                Orientation.Vertical,
                enabled = true
            )
        ) {
            if (pageList.isNotEmpty()) {
                pageList.forEachIndexed { index, page ->
                    PageItem(
                        pageNumber = index + 1,
                        index = index,
                        pageItemClicked = {
                            pageItemClicked(it)
                        },
                        retakeButtonClicked = {
                            retakeButtonClicked(it)
                        },
                        deleteButtonClicked = {
                            deleteButtonClicked()
                        }
                    )
                    Divider(
                        color = borderGreyLight,
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 26.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp)
            ) {
                Row(
                    Modifier
                        .padding(vertical = 2.dp)
                        .align(Alignment.CenterStart)
                        .clickable {
                            addPageClicked()
                        }
                        .then(modifier),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        painter = painterResource(id = R.drawable.sharp_add_circle_outline_24),
                        contentDescription = null,
                        tint = textColorDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Page",
                        color = textColorDark,
                        style = buttonTextStyle,
                        modifier = Modifier.absolutePadding(bottom = 3.dp)
                    )

                }

                Row(
                    Modifier
                        .padding(vertical = 2.dp)
                        .align(Alignment.CenterEnd)
                        .clickable {
                            deleteButtonClicked()
                        }
                        .then(modifier),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_delete_icon),
                        contentDescription = "delete form image",
                        tint = redOffline,
                        modifier = Modifier
                            .size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Delete & Retake",
                        color = textColorDark,
                        style = buttonTextStyle,
                        modifier = Modifier.absolutePadding(bottom = 3.dp)
                    )
                }
            }
            if (pageList.size >= 4) {
                Text(
                    text = "If more than 5 pages, please upload the last page.",
                    style = smallTextStyle,
                    color = textColorDark80,
                    modifier = Modifier.padding(horizontal = 26.dp)
                )
            }
        }
    }
}

@Composable
fun PageItem(
    modifier: Modifier = Modifier,
    pageNumber: Int,
    index: Int,
    pageItemClicked: (pageNumber: Int) -> Unit,
    retakeButtonClicked: (index: Int) -> Unit,
    deleteButtonClicked: (index: Int) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 26.dp)
            .clickable {
                pageItemClicked(pageNumber)
            }
            .then(modifier),
    ) {
        Text(
            text = "Page $pageNumber",
            textAlign = TextAlign.Start,
            fontSize = 14.sp,
            fontFamily = NotoSans,
            fontWeight = FontWeight.SemiBold,
            color = textColorDark,
            modifier = Modifier
                .padding(top = if (index == 0) 0.dp else 8.dp, bottom = 8.dp, end = 32.dp)
                .align(Alignment.CenterStart)
                .fillMaxWidth()
                .indication(
                    interactionSource = interactionSource,
                    indication = rememberRipple(
                        bounded = true,
                        color = Color.Black
                    )
                )
        )
        Spacer(modifier = Modifier.width(2.dp))
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd), horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_retake_24),
                contentDescription = null,
                tint = textColorDark,
                modifier = Modifier.clickable {
                    Log.d("FormPictureScreen", "retakeButtonClicked(index): $index")
                    retakeButtonClicked(index)
                }
            )
            /*Spacer(modifier = Modifier.width(2.dp))
            Icon(
                painter = painterResource(id = R.drawable.baseline_delete_icon),
                contentDescription = "delete form image",
                tint = redOffline,
                modifier = Modifier.size(24.dp).clickable {
                    deleteButtonClicked(index)
                }
            )*/
        }
    }
}