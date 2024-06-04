package com.patsurvey.nudge.activities.ui.vo_endorsement

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.nudge.core.toDateInMMDDYYFormat
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.ui.socialmapping.ShowDialog
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.black20
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.borderGreyLight
import com.patsurvey.nudge.activities.ui.theme.mediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.redDark
import com.patsurvey.nudge.activities.ui.theme.redOffline
import com.patsurvey.nudge.activities.ui.theme.smallTextStyle
import com.patsurvey.nudge.activities.ui.theme.smallTextStyleMediumWeight
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.activities.ui.theme.textColorDark80
import com.patsurvey.nudge.activities.ui.theme.white
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.navigation.selection.VoEndorsmentScreeens
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.DoubleButtonBox
import com.patsurvey.nudge.utils.EXPANSTION_TRANSITION_DURATION
import com.patsurvey.nudge.utils.FORM_C
import com.patsurvey.nudge.utils.FORM_D
import com.patsurvey.nudge.utils.NudgeCore.getBengalString
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.PREF_NEED_TO_POST_FORM_C_AND_D_
import com.patsurvey.nudge.utils.openSettings
import com.patsurvey.nudge.utils.showToast
import com.patsurvey.nudge.utils.uriFromFile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun FormPictureScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    formPictureScreenViewModel: FormPictureScreenViewModel,
    stepId: Int
) {

    val localContext = LocalContext.current

    val screenHeight = LocalConfiguration.current.screenHeightDp

    val shouldRequestPermission = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = localContext) {

        formPictureScreenViewModel.setUpOutputDirectory(localContext as MainActivity)
        requestCameraPermission(localContext as Activity, formPictureScreenViewModel) {
            shouldRequestPermission.value = true
        }
        formPictureScreenViewModel.isFormAAvailableForVillage(
            context = localContext,
            villageId = formPictureScreenViewModel.repository.prefRepo.getSelectedVillage().id
        )
        formPictureScreenViewModel.isFormBAvailableForVillage(
            context = localContext,
            villageId = formPictureScreenViewModel.repository.prefRepo.getSelectedVillage().id
        )
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

    var requestId by remember { mutableStateOf(0) }

    var imageRequest = ImageRequest.Builder(localContext)
        .data(File(formPictureScreenViewModel.imagePath.value))
        .memoryCachePolicy(CachePolicy.DISABLED)
        .diskCachePolicy(CachePolicy.DISABLED)
        .setParameter("requestId", requestId, memoryCacheKey = null)
        .build()

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
                    AsyncImage(
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
                        model = imageRequest,
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

                val (bottomActionBox, mainBox, formBox) = createRefs()
                Box(modifier = Modifier
                    .wrapContentHeight()
                    .constrainAs(mainBox) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
                    .padding(horizontal = 16.dp)
                    .padding(bottom = bottomPadding)
                ) {

                    Column {
                        val cameraLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.TakePicture(),
                            onResult = { success ->
                                if (success) {
                                    formPictureScreenViewModel.photoUri = formPictureScreenViewModel.tempUri
                                    handleImageCapture(formPictureScreenViewModel.photoUri, formPictureScreenViewModel.imagePathForCapture, context = (context as MainActivity), formName = formPictureScreenViewModel.getFormSubPath(
                                        formPictureScreenViewModel.shouldShowCamera.value.first
                                        , if (formPictureScreenViewModel.retakeImageIndex.value != -1)formPictureScreenViewModel.formCPageList.value.size + 1 else formPictureScreenViewModel.retakeImageIndex.value + 1), viewModal = formPictureScreenViewModel)
                                    formPictureScreenViewModel.shouldShowCamera.value = Pair("", false)
                                } else {
                                    formPictureScreenViewModel.shouldShowCamera.value = Pair("", false)
                                    formPictureScreenViewModel.tempUri = Uri.EMPTY
                                    formPictureScreenViewModel.imagePathForCapture = ""
                                }
                            }
                        )

                        AnimatedVisibility(visible = !formPictureScreenViewModel.shouldShowCamera.value.second && !formPictureScreenViewModel.shouldShowPhoto.value) {
                            Column(
                                modifier = modifier
                                    .padding(top = 14.dp),
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
                                    prefRepo = formPictureScreenViewModel.repository.prefRepo,
                                    modifier = Modifier.fillMaxWidth(),
                                    startPadding = 0.dp
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState())
                                        .padding(vertical = 14.dp),
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
                                        showIcon = formPictureScreenViewModel.formCPageList.value.isEmpty(),
                                        cardTitle = if (formPictureScreenViewModel.formCPageList.value.isEmpty()) stringResource(
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
                                                    formPictureScreenViewModel.repository.prefRepo.getPref(
                                                        formPictureScreenViewModel.getFormPathKey(
                                                            formPictureScreenViewModel.pageItemClicked.value
                                                        ),
                                                        ""
                                                    )?.let { if (it.isNotEmpty()) it else "" }
                                                        .toString()
                                                if (!formPictureScreenViewModel.imagePath.value.isNullOrEmpty())
                                                    formPictureScreenViewModel.setUri(localContext)
                                                imageRequest = ImageRequest.Builder(localContext)
                                                    .data(File(formPictureScreenViewModel.imagePath.value))
                                                    .memoryCachePolicy(CachePolicy.DISABLED)
                                                    .diskCachePolicy(CachePolicy.DISABLED)
                                                    .setParameter("requestId", requestId, memoryCacheKey = null)
                                                    .build()
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
                                                        NudgeLogger.d("FormPictureScreen", "Permission previously granted")

                                                        if (formPictureScreenViewModel.formCPageList.value.size < 5) {
                                                            formPictureScreenViewModel.shouldShowCamera.value =
                                                                Pair(FORM_C, true)
                                                            val formName = formPictureScreenViewModel.getFormSubPath(
                                                                formPictureScreenViewModel.shouldShowCamera.value.first,
                                                                formPictureScreenViewModel.formCPageList.value.size + 1
                                                            ) + "_" + System.currentTimeMillis()
                                                                .toDateInMMDDYYFormat()
                                                            val imageFile = formPictureScreenViewModel.getImageFileName(context, formName)
                                                            formPictureScreenViewModel.imagePathForCapture = imageFile.absolutePath
                                                            val uri = uriFromFile(context = context, imageFile)
                                                            formPictureScreenViewModel.tempUri = uri
                                                            cameraLauncher.launch(uri)
                                                        } else {
                                                            showToast(
                                                                localContext,
                                                                "Max 5 Pages can be captured"
                                                            )
                                                        }

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
                                                        NudgeLogger.d("FormPictureScreen", "Show camera permissions dialog")
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
                                                        NudgeLogger.d("FormPictureScreen: ", "permission not granted")
                                                        shouldRequestPermission.value = true
                                                    }
                                                }
                                            } else {
                                                when {
                                                    ContextCompat.checkSelfPermission(
                                                        localContext as Activity,
                                                        Manifest.permission.CAMERA
                                                    ) == PackageManager.PERMISSION_GRANTED -> {
                                                        NudgeLogger.d("PatImagePreviewScreen", "Permission previously granted")

                                                        if (formPictureScreenViewModel.formCPageList.value.size < 5) {
                                                            formPictureScreenViewModel.shouldShowCamera.value = Pair(FORM_C, true)
                                                            val formName = formPictureScreenViewModel.getFormSubPath(
                                                                FORM_C,
                                                                formPictureScreenViewModel.formCPageList.value.size + 1
                                                            ) + "_" + System.currentTimeMillis()
                                                                .toDateInMMDDYYFormat()
                                                            val imageFile = formPictureScreenViewModel.getImageFileName(context, formName)
                                                            formPictureScreenViewModel.imagePathForCapture = imageFile.absolutePath
                                                            val uri = uriFromFile(context = context, imageFile)
                                                            formPictureScreenViewModel.tempUri = uri
                                                            cameraLauncher.launch(uri)
                                                        } else {
                                                            showToast(
                                                                localContext,
                                                                "Max 5 Pages can be captured"
                                                            )
                                                        }
                                                    }

                                                    ActivityCompat.shouldShowRequestPermissionRationale(
                                                        localContext as Activity,
                                                        Manifest.permission.CAMERA
                                                    ) -> {
                                                        NudgeLogger.d("PatImagePreviewScreen", "Show camera permissions dialog")
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
                                                        NudgeLogger.d("requestCameraPermission: ", "permission not granted")
                                                        shouldRequestPermission.value = true
                                                    }
                                                }
                                            }
                                        },
                                        retakeButtonClicked = { index ->

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
                                                            "FormPictureScreen",
                                                            "Permission previously granted"
                                                        )
                                                        formPictureScreenViewModel.retakeImageIndex.value = index
                                                        formPictureScreenViewModel.shouldShowCamera.value =
                                                            Pair(FORM_C, true)
                                                        val formName =
                                                            formPictureScreenViewModel.getFormSubPath(
                                                                formPictureScreenViewModel.shouldShowCamera.value.first,
                                                                index + 1
                                                            ) + "_" + System.currentTimeMillis()
                                                                .toDateInMMDDYYFormat()
                                                        val imageFile =
                                                            formPictureScreenViewModel.getImageFileName(
                                                                context,
                                                                formName
                                                            )
                                                        formPictureScreenViewModel.imagePathForCapture =
                                                            imageFile.absolutePath
                                                        val uri =
                                                            uriFromFile(context = context, imageFile)
                                                        formPictureScreenViewModel.tempUri = uri
                                                        cameraLauncher.launch(uri)

                                                        /*val imageFile = patDidiSummaryViewModel.getFileName(localContext, didi.value)
                                                        patDidiSummaryViewModel.imagePath = imageFile.absolutePath
                                                        val uri = uriFromFile(localContext, imageFile)
                                                        patDidiSummaryViewModel.tempUri = uri
                                                        cameraLauncher.launch(uri)*/
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
                                                        NudgeLogger.d("FormPictureScreen", "Show camera permissions dialog")
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
                                                        NudgeLogger.d("FormPictureScreen: ", "permission not granted")
                                                        shouldRequestPermission.value = true
                                                    }
                                                }
                                            } else {
                                                when {
                                                    ContextCompat.checkSelfPermission(
                                                        localContext as Activity,
                                                        Manifest.permission.CAMERA
                                                    ) == PackageManager.PERMISSION_GRANTED -> {
                                                        NudgeLogger.d("PatImagePreviewScreen", "Permission previously granted")

                                                        formPictureScreenViewModel.retakeImageIndex.value =
                                                            index
                                                        formPictureScreenViewModel.shouldShowCamera.value =
                                                            Pair(FORM_C, true)
                                                        val formName =
                                                            formPictureScreenViewModel.getFormSubPath(
                                                                formPictureScreenViewModel.shouldShowCamera.value.first,
                                                                index + 1
                                                            ) + "_" + System.currentTimeMillis()
                                                                .toDateInMMDDYYFormat()
                                                        val imageFile =
                                                            formPictureScreenViewModel.getImageFileName(
                                                                context,
                                                                formName
                                                            )
                                                        formPictureScreenViewModel.imagePathForCapture =
                                                            imageFile.absolutePath
                                                        val uri =
                                                            uriFromFile(context = context, imageFile)
                                                        formPictureScreenViewModel.tempUri = uri
                                                        cameraLauncher.launch(uri)
                                                    }

                                                    ActivityCompat.shouldShowRequestPermissionRationale(
                                                        localContext as Activity,
                                                        Manifest.permission.CAMERA
                                                    ) -> {
                                                        NudgeLogger.d("PatImagePreviewScreen", "Show camera permissions dialog")
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
                                                        NudgeLogger.d("requestCameraPermission: ", "permission not granted")
                                                        shouldRequestPermission.value = true
                                                    }
                                                }
                                            }

                                            formPictureScreenViewModel.retakeImageIndex.value =
                                                index
//                                        formPictureScreenViewModel.setCameraExecutor()
                                            formPictureScreenViewModel.shouldShowCamera.value =
                                                Pair(FORM_C, true)
                                            val imageToBeReplaced =
                                                formPictureScreenViewModel.formCImageList.value["Page_${index + 1}"]
                                        },
                                        deleteButtonClicked = {
                                            formPictureScreenViewModel.formCPageList.value = mutableListOf()
                                            formPictureScreenViewModel.formCImageList.value =  mutableMapOf()
                                            formPictureScreenViewModel.formsCClicked.value = --formPictureScreenViewModel.formsCClicked.value
                                            for (i in 1..5) {
                                                formPictureScreenViewModel.repository.prefRepo.savePref(formPictureScreenViewModel.getFormPathKey(formPictureScreenViewModel.getFormSubPath(FORM_C, i)), "")
                                            }
                                        }
                                    )


                                    FormPictureCard(
                                        modifier = Modifier,
                                        navController = navController,
                                        showIcon = formPictureScreenViewModel.formDPageList.value.isEmpty(),
                                        cardTitle = if (formPictureScreenViewModel.formDPageList.value.isEmpty()) stringResource(
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
                                                    formPictureScreenViewModel.repository.prefRepo.getPref(
                                                        formPictureScreenViewModel.getFormPathKey(
                                                            formPictureScreenViewModel.pageItemClicked.value
                                                        ),
                                                        ""
                                                    )?.let { if (it.isNotEmpty()) it else "" }
                                                        .toString()
                                                if (!formPictureScreenViewModel.imagePath.value.isNullOrEmpty())
                                                    formPictureScreenViewModel.setUri(localContext)
                                                imageRequest = ImageRequest.Builder(localContext)
                                                    .data(File(formPictureScreenViewModel.imagePath.value))
                                                    .memoryCachePolicy(CachePolicy.DISABLED)
                                                    .diskCachePolicy(CachePolicy.DISABLED)
                                                    .setParameter("requestId", requestId, memoryCacheKey = null)
                                                    .build()
                                                delay(250)
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
                                                        NudgeLogger.d("FormPictureScreen", "Permission previously granted")

                                                        if (formPictureScreenViewModel.formDPageList.value.size < 5) {
                                                            formPictureScreenViewModel.shouldShowCamera.value =
                                                                Pair(FORM_D, true)
                                                            val formName = formPictureScreenViewModel.getFormSubPath(
                                                                formPictureScreenViewModel.shouldShowCamera.value.first,
                                                                formPictureScreenViewModel.formDPageList.value.size + 1
                                                            ) + "_" + System.currentTimeMillis()
                                                                .toDateInMMDDYYFormat()
                                                            val imageFile = formPictureScreenViewModel.getImageFileName(context, formName)
                                                            formPictureScreenViewModel.imagePathForCapture = imageFile.absolutePath
                                                            val uri = uriFromFile(context = context, imageFile)
                                                            formPictureScreenViewModel.tempUri = uri
                                                            cameraLauncher.launch(uri)
                                                        } else {
                                                            showToast(
                                                                localContext,
                                                                "Max 5 Pages can be captured"
                                                            )
                                                        }
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
                                                        NudgeLogger.d("FormPictureScreen", "Show camera permissions dialog")
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
                                                        NudgeLogger.d("FormPictureScreen: ", "permission not granted")
                                                        shouldRequestPermission.value = true
                                                    }
                                                }
                                            } else {
                                                when {
                                                    ContextCompat.checkSelfPermission(
                                                        localContext as Activity,
                                                        Manifest.permission.CAMERA
                                                    ) == PackageManager.PERMISSION_GRANTED -> {
                                                        NudgeLogger.d("PatImagePreviewScreen", "Permission previously granted")

                                                        if (formPictureScreenViewModel.formDPageList.value.size < 5) {
                                                            formPictureScreenViewModel.shouldShowCamera.value = Pair(FORM_D, true)
                                                            val formName = formPictureScreenViewModel.getFormSubPath(
                                                                FORM_D,
                                                                formPictureScreenViewModel.formDPageList.value.size + 1
                                                            ) + "_" + System.currentTimeMillis()
                                                                .toDateInMMDDYYFormat()
                                                            val imageFile = formPictureScreenViewModel.getImageFileName(context, formName)
                                                            formPictureScreenViewModel.imagePathForCapture = imageFile.absolutePath
                                                            val uri = uriFromFile(context = context, imageFile)
                                                            formPictureScreenViewModel.tempUri = uri
                                                            cameraLauncher.launch(uri)
                                                        } else {
                                                            showToast(
                                                                localContext,
                                                                "Max 5 Pages can be captured"
                                                            )
                                                        }
                                                    }

                                                    ActivityCompat.shouldShowRequestPermissionRationale(
                                                        localContext as Activity,
                                                        Manifest.permission.CAMERA
                                                    ) -> {
                                                        NudgeLogger.d("PatImagePreviewScreen", "Show camera permissions dialog")
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
                                                        NudgeLogger.d("requestCameraPermission: ", "permission not granted")
                                                        shouldRequestPermission.value = true
                                                    }
                                                }
                                            }
                                        },
                                        retakeButtonClicked = { index ->

                                            formPictureScreenViewModel.retakeImageIndex.value =
                                                index
                                            formPictureScreenViewModel.shouldShowCamera.value =
                                                Pair(FORM_D, true)
                                            val formName = formPictureScreenViewModel.getFormSubPath(
                                                formPictureScreenViewModel.shouldShowCamera.value.first,
                                                formPictureScreenViewModel.formDPageList.value.size + 1
                                            ) + "_" + System.currentTimeMillis()
                                                .toDateInMMDDYYFormat()
                                            val imageFile = formPictureScreenViewModel.getImageFileName(context, formName)
                                            formPictureScreenViewModel.imagePathForCapture = imageFile.absolutePath
                                            val uri = uriFromFile(context = context, imageFile)
                                            formPictureScreenViewModel.tempUri = uri
                                            cameraLauncher.launch(uri)
                                        },
                                        deleteButtonClicked = {
                                            formPictureScreenViewModel.formDPageList.value = mutableListOf()
                                            formPictureScreenViewModel.formDImageList.value =  mutableMapOf()
                                            formPictureScreenViewModel.formsDClicked.value = --formPictureScreenViewModel.formsDClicked.value
                                            for (i in 1..5) {
                                                formPictureScreenViewModel.repository.prefRepo.savePref(formPictureScreenViewModel.getFormPathKey(formPictureScreenViewModel.getFormSubPath(FORM_D, i)), "")
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = stringResource(id = R.string.reference_of_forms),
                                style = TextStyle(
                                    fontFamily = NotoSans,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = textColorDark,
                                modifier = Modifier.clickable {
                                }
                            )

                            formLinkView(linkText = stringResource(id = R.string.link_of_a)) {
                                formPictureScreenViewModel.showLoaderForTime(500)
                                if (formPictureScreenViewModel.formAAvailable.value) {
                                    navController.navigate(VoEndorsmentScreeens.FORM_A_SCREEN.route)
                                } else
                                    showToast(
                                        context,
                                        context.getString(R.string.no_data_form_a_not_generated_text)
                                    )
                            }
                            formLinkView(linkText = stringResource(id = R.string.link_of_b) ) {
                                formPictureScreenViewModel.showLoaderForTime(500)
                                if (formPictureScreenViewModel.formBAvailable.value) {
                                    navController.navigate(VoEndorsmentScreeens.FORM_B_SCREEN.route)
                                } else
                                    showToast(
                                        context,
                                        context.getString(R.string.no_data_form_a_not_generated_text)
                                    )
                            }
                        }
                    }



                }





                if (!formPictureScreenViewModel.shouldShowCamera.value.second && formPictureScreenViewModel.formCPageList.value.isNotEmpty() && formPictureScreenViewModel.formDPageList.value.isNotEmpty()) {
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
                            NudgeLogger.d("FormPictureScreen", "submit button clicked")
                            formPictureScreenViewModel.repository.prefRepo.savePref(
                                PREF_NEED_TO_POST_FORM_C_AND_D_ + formPictureScreenViewModel.repository.prefRepo.getSelectedVillage().id,true)
                            formPictureScreenViewModel.updateVoEndorsementEditFlag()
                            formPictureScreenViewModel.updateDidiVoEndorsementStatus()
                            formPictureScreenViewModel.markVoEndorsementComplete(
                                formPictureScreenViewModel.repository.prefRepo.getSelectedVillage().id,
                                stepId
                            )
                            formPictureScreenViewModel.addRankingFlagEditEvent(stepId = stepId)
                            formPictureScreenViewModel.saveVoEndorsementDate()
                            formPictureScreenViewModel.uploadFormsCAndD(
                                context,
                                (context as MainActivity).isOnline.value
                            )


                            navController.navigate(
                                "vo_endorsement_step_completion_screen/${
                                    getBengalString(context,formPictureScreenViewModel.getStateId(),R.plurals.vo_endorsement_completed_message)
                                        .replace(
                                            "{VILLAGE_NAME}",
                                            formPictureScreenViewModel.villageEntity.value?.name ?: BLANK_STRING
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

//    viewModal.cameraExecutor.shutdown()

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
            viewModal.shouldShowCamera.value = Pair("", false)
            viewModal.tempUri = Uri.EMPTY
            viewModal.imagePathForCapture = ""
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

            if (viewModal.formsCClicked.value <= 1) {
                viewModal.formsCClicked.value = viewModal.formsCClicked.value + 1
            }
            viewModal.shouldShowCamera.value = Pair("", false)
            viewModal.tempUri = Uri.EMPTY
            viewModal.imagePathForCapture = ""
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

            if (viewModal.formsDClicked.value <= 1) {
                viewModal.formsDClicked.value = viewModal.formsDClicked.value + 1
            }
        }
    }
    viewModal.shouldShowCamera.value = Pair("", false)
}

private fun requestCameraPermission(context: Activity, viewModal: FormPictureScreenViewModel, requestPermission: () -> Unit) {

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
                Log.i("FormPictureScreen", "Permission previously granted")
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
                Log.i("FormPictureScreen", "Show camera permissions dialog")
                viewModal.shouldShowCamera.value = Pair("", false)
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
                viewModal.shouldShowCamera.value = Pair("", false)
                Log.d("requestCameraPermission: ", "permission not granted")
                requestPermission()
            }
        }
    } else {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("FormPictureScreen", "Permission previously granted")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                Manifest.permission.CAMERA
            ) -> {
                Log.i("FormPictureScreen", "Show camera permissions dialog")
                viewModal.shouldShowCamera.value = Pair("", false)
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
                viewModal.shouldShowCamera.value = Pair("", false)
                Log.d("requestCameraPermission: ", "permission not granted")
                requestPermission()
            }
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
                if (showIcon && !expanded) {
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
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp)
            ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(blueDark)
                        .padding(vertical = 6.dp)
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(
                                bounded = true,
                                color = Color.White
                            )

                        ) {
                            addPageClicked()
                        }
                        .then(modifier),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(blueDark)
                            .align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Icon(
                            painter = painterResource(id = R.drawable.sharp_add_circle_outline_24),
                            contentDescription = null,
                            tint = white,
                            modifier = Modifier
                                .size(26.dp)
                                .background(Color.Transparent)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(id = R.string.take_photo_text),
                            color = white,
                            style = smallTextStyle,
                            modifier = Modifier
                                .absolutePadding(bottom = 3.dp)
                                .background(Color.Transparent)
                        )

                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .border(
                            width = 1.dp,
                            color = redDark,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(vertical = 4.dp)
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(
                                bounded = true,
                                color = Color.White
                            )

                        ) {
                            deleteButtonClicked()
                        }
                        .then(modifier),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        Modifier
                            .padding(vertical = 2.dp)
                            .background(Color.Transparent)
                            .align(Alignment.Center),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_delete_icon),
                            contentDescription = "delete form image",
                            tint = redOffline,
                            modifier = Modifier
                                .size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(id = R.string.delete_and_retake),
                            color = redDark,
                            style = smallTextStyle,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2,
                            modifier = Modifier.absolutePadding(bottom = 3.dp)
                        )
                    }
                }
            }
            if (pageList.size >= 4) {
                Text(
                    text = stringResource(id = R.string.if_more_than_5_pages_please_uload_last_page),
                    style = smallTextStyle,
                    color = textColorDark80,
                    modifier = Modifier.padding(horizontal = 26.dp)
                )
            }
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(10.dp))
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
            text = stringResource(R.string.page_name_text) + " " + pageNumber,
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


    @Composable
    fun formLinkView(linkText: String, onLinkClick: () -> Unit) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                Text(
                    text = linkText,
                    style = TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline
                    ),
                    color = textColorDark,
                    modifier = Modifier.clickable {
                        onLinkClick()
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))

            }
        }
    }


@Preview(showBackground = true)
@Composable
fun formAAndBLinksPreview(){
    formLinkView(linkText = "Link To Form A", onLinkClick = {})
}
