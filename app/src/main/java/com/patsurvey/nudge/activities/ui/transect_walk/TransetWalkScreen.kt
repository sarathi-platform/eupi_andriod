package com.patsurvey.nudge.activities.ui.transect_walk

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.MainActivity
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.largeTextStyle
import com.patsurvey.nudge.activities.ui.theme.mediumTextStyle
import com.patsurvey.nudge.activities.ui.theme.smallTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.customviews.CustomProgressBar
import com.patsurvey.nudge.customviews.ModuleAddedSuccessView
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.intefaces.LocalDbListener
import com.patsurvey.nudge.intefaces.NetworkCallbackListener
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.BlueButtonWithIconWithFixedWidth
import com.patsurvey.nudge.utils.BlueButtonWithIconWithFixedWidthWithoutIcon
import com.patsurvey.nudge.utils.ButtonOutline
import com.patsurvey.nudge.utils.DoubleButtonBox
import com.patsurvey.nudge.utils.EMPTY_TOLA_NAME
import com.patsurvey.nudge.utils.LocationCoordinates
import com.patsurvey.nudge.utils.LocationUtil
import com.patsurvey.nudge.utils.NudgeCore.getBengalString
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.StepStatus
import com.patsurvey.nudge.utils.Tola
import com.patsurvey.nudge.utils.TolaStatus
import com.patsurvey.nudge.utils.showCustomToast

@Composable
fun TransectWalkScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: TransectWalkViewModel,
    villageId: Int,
    stepId: Int
) {

    LaunchedEffect(key1 = true) {
        viewModel.fetchTolaList(villageId)
        viewModel.isTransectWalkComplete(stepId,villageId)
//        viewModel.isVoEndorsementCompleteForVillage(villageId)
    }
    var showAddTolaBox by remember { mutableStateOf(false) }
    val tolaList by viewModel.tolaList.collectAsState()
    val tolaToBeEdited: Tola by remember { mutableStateOf(Tola()) }
    var completeTolaAdditionClicked by remember { mutableStateOf(false) }
    val isTolaEdit = remember { mutableStateOf(false) }
    val isTolaAdded = remember {
        mutableStateOf(0)
    }
    var mEditedTola:TolaEntity?=null
    val networkError = viewModel.networkErrorMessage.value

    val context = LocalContext.current
    val localDensity = LocalDensity.current

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    val focusManager = LocalFocusManager.current

    var bottomPadding by remember {
        mutableStateOf(0.dp)
    }

    DisposableEffect(key1 = Unit) {
        LocationUtil.setLocation((context as MainActivity))
        onDispose {
            LocationUtil.location = LocationCoordinates(0.0, 0.0)
        }
    }

    BackHandler() {
        if (completeTolaAdditionClicked)
            completeTolaAdditionClicked = false
        else {
            navController.popBackStack()
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(true) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            .then(modifier)
    ) {
        viewModel.setVillage(villageId)
//        if (networkError.isNotEmpty())
//            showCustomToast(context, SYNC_FAILED)
        val (bottomActionBox, mainBox) = createRefs()

        Box(modifier = Modifier
            .constrainAs(mainBox) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }
            .padding(top = 14.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 20.dp)
            ) {
                VillageDetailView(
                    villageName = viewModel.getSelectedVillage().name ?: BLANK_STRING,
                    voName = viewModel.getSelectedVillage().federationName ?: BLANK_STRING,
                    modifier = Modifier,
                    stateId = viewModel.getStateId()
                )
                val tolaTolaCount = tolaList.filter { it.status == TolaStatus.TOLA_ACTIVE.ordinal }.size
                val totalCountWithoutEmptyTola = tolaList.filter { it.status == TolaStatus.TOLA_ACTIVE.ordinal && it.name != EMPTY_TOLA_NAME }.size
                ModuleAddedSuccessView(completeAdditionClicked = completeTolaAdditionClicked,
                    message = if (tolaList.map { it.name }.contains(EMPTY_TOLA_NAME) && tolaTolaCount == 1)
                        stringResource(R.string.empty_tola_success_message)
                    else
                        stringResource(
                            if (totalCountWithoutEmptyTola < 2)
                                R.string.tola_conirmation_text_singular else R.string.tola_conirmation_text_plural,
                            totalCountWithoutEmptyTola
                    ),
                    Modifier.padding(vertical = (screenHeight/4).dp)
                )
                val listState = rememberLazyListState()
                val coroutineScope = rememberCoroutineScope()
                LazyColumn(
                    modifier = Modifier.padding(bottom = bottomPadding),state = listState) {

                    if (viewModel.showLoader.value) {
                        item { CustomProgressBar(modifier = Modifier) }
                    } else {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (tolaList.isNotEmpty() || showAddTolaBox) {
                                    Text(
                                        text = stringResource(id = R.string.transect_wale_title),
                                        style = mediumTextStyle,
                                        color = blueDark,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                                if (tolaList.isNotEmpty()) {
                                    Spacer(modifier = Modifier.padding(14.dp))
//                                    if (!viewModel.isVoEndorsementComplete.value) {
                                        ButtonOutline(
                                            modifier = Modifier
                                                .weight(0.9f),
                                        ) {
                                            if (!showAddTolaBox)
                                                showAddTolaBox = true
                                        }
//                                    }

                                }
                            }
                        }
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 0.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                if (tolaList.isNotEmpty()) {
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(
                                                style = SpanStyle(
                                                    color = textColorDark,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Normal,
                                                    fontFamily = NotoSans
                                                )
                                            ) {
                                                append(stringResource(id = R.string.showing))
                                            }
                                            withStyle(
                                                style = SpanStyle(
                                                    color = textColorDark,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontFamily = NotoSans
                                                )
                                            ) {
                                                append(" ${tolaList.filter { it.name != EMPTY_TOLA_NAME }.size}")
                                            }
                                            withStyle(
                                                style = SpanStyle(
                                                    color = textColorDark,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Normal,
                                                    fontFamily = NotoSans
                                                )
                                            ) {
                                                append(
                                                    if (tolaList.filter { it.name != EMPTY_TOLA_NAME }.size > 1)
                                                        stringResource(R.string.added_tolas_text_plural)
                                                    else
                                                        stringResource(R.string.added_tolas_text_singular)
                                                )
                                            }
                                        }
                                    )
                                }

                                AnimatedVisibility(visible = showAddTolaBox) {
                                    AddTolaBox(
                                        tolaName = tolaToBeEdited.name,
                                        isLocationAvailable = (tolaToBeEdited.location.lat != null && tolaToBeEdited.location.long != null),
                                        onSaveClicked = { name, location ->
                                            viewModel.addTola(
                                                Tola(
                                                    name,
                                                    location ?: LocationCoordinates()
                                                ),
                                                object : LocalDbListener {
                                                    override fun onInsertionSuccess() {
                                                        showAddTolaBox = false
                                                        focusManager.clearFocus()
                                                        showCustomToast(
                                                            context,
                                                            context.getString(R.string.tola_successfully_added)
                                                                .replace("{TOLA_NAME}", name)
                                                        )
                                                        viewModel.markTransectWalkIncomplete(
                                                            stepId,
                                                            villageId,
                                                            (context as MainActivity).isOnline.value ?: false,
                                                            object : NetworkCallbackListener {
                                                                override fun onSuccess() {

                                                                }

                                                                override fun onFailed() {

                                                                }
                                                            })
                                                    }
                                                    override fun onInsertionFailed() {
                                                        showCustomToast(context,context.getString(R.string.tola_already_exist))
                                                    }
                                                }
                                            )
                                        },
                                        onCancelClicked = {
                                            showAddTolaBox = false
                                        }
                                    )
                                }
                            }
                        }

                        if (tolaList.isEmpty() && !showAddTolaBox) {
                            item {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(vertical = (screenHeight / 4).dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.transect_wale_title),
                                            style = largeTextStyle,
                                            color = blueDark,
                                            modifier = Modifier,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(
                                                    style = SpanStyle(
                                                        color = textColorDark,
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Normal,
                                                        fontFamily = NotoSans
                                                    )
                                                ) {
                                                    append(stringResource(R.string.empty_tola_string))
                                                }
                                            },
                                            modifier = Modifier.padding(top = 32.dp)
                                        )
                                        BlueButtonWithIconWithFixedWidth(
                                            buttonText = stringResource(id = R.string.add_tola),
                                            icon = Icons.Default.Add,
                                            modifier = Modifier.padding(top = 16.dp)
                                        ) {
                                            isTolaEdit.value=false
                                            if (!showAddTolaBox)
                                                showAddTolaBox = true
                                        }
                                        //TODO fix empty tola functionality
                                        Text(
                                            text = "or",
                                            color = textColorDark,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Normal,
                                            fontFamily = NotoSans,
                                            modifier = Modifier.padding(top = 16.dp)
                                        )
                                       /* Text(
                                            text = "Continue Without Tola",
                                            color = textColorDark,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Normal,
                                            fontFamily = NotoSans,
                                            modifier = Modifier.padding(top = 32.dp)
                                        )*/
                                        BlueButtonWithIconWithFixedWidthWithoutIcon(
                                            buttonText = stringResource(R.string.empty_tola_button_text),
                                            modifier = Modifier.padding(top = 16.dp)
                                        ) {
                                            viewModel.addEmptyTola()
                                            completeTolaAdditionClicked = true
                                        }

                                    }
                                }
                            }
                        } else {
                            itemsIndexed(tolaList.filter { it.name != EMPTY_TOLA_NAME }) { index, tola ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    TolaBox(
                                        tolaName = tola.name,
                                        tolaLocation = LocationCoordinates(
                                            tola.latitude,
                                            tola.longitude
                                        ),
                                        isLocationAvailable = (tola.latitude != 0.0 && tola.longitude != 0.0),
                                        isTransectWalkCompleted = (viewModel.isTransectWalkComplete.value && !tola.needsToPost),
                                        listState = listState,
                                        coroutineScope = coroutineScope,
                                        index = index,
                                        deleteButtonClicked = {
                                            viewModel.removeTola(tola.id, context = context, isOnline = (context as MainActivity).isOnline.value ?: false,  object : NetworkCallbackListener{
                                                override fun onSuccess() {
                                                    showCustomToast(context,context.getString(R.string.tola_deleted).replace("{TOLA_NAME}", tola.name))
                                                    showAddTolaBox = false
                                                }

                                                override fun onFailed() {
//                                                    showCustomToast(context, SYNC_FAILED)
                                                }
                                            }, villageId = villageId, stepId = stepId)
//                                            showCustomToast(context,context.getString(R.string.tola_deleted).replace("{TOLA_NAME}", tola.name))
                                        },
                                        saveButtonClicked = { newName, newLocation ->
                                           showAddTolaBox = if (newName == tola.name && (newLocation?.lat == tola.latitude && newLocation.long == tola.longitude)) false
                                           else {
                                               viewModel.updateTola(tola.id, newName, newLocation, isOnline = (context as MainActivity).isOnline.value ?: false, object : NetworkCallbackListener{
                                                   override fun onSuccess() {
                                                   }

                                                   override fun onFailed() {
//                                                       showCustomToast(context, SYNC_FAILED)
                                                   }
                                               })
                                               /*viewModel.markTransectWalkIncomplete(stepId, villageId, object : NetworkCallbackListener{
                                                   override fun onSuccess() {
                                                   }

                                                   override fun onFailed() {
//                                                       showCustomToast(context, SYNC_FAILED)
                                                   }
                                               })*/
                                               showCustomToast(context,context.getString(R.string.tola_updated).replace("{TOLA_NAME}", newName))
                                               false
                                           }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (tolaList.isNotEmpty() && !viewModel.isTransectWalkComplete.value) { //Check if we have to mark transect walk in progress if after completion a new tola is added?
            DoubleButtonBox(
                modifier = Modifier
                    .constrainAs(bottomActionBox) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                    .onGloballyPositioned { coordinates ->
                        bottomPadding = with(localDensity) {
                            coordinates.size.height.toDp()
                        }
                    },

                positiveButtonText = if (completeTolaAdditionClicked) stringResource(id = R.string.complete_transect_walk_text) else stringResource(
                    id = R.string.mark_complete_text
                ),
                negativeButtonRequired = false,
                positiveButtonOnClick = {
                    if (completeTolaAdditionClicked) {
                        viewModel.saveTransectWalkCompletionDate()
                        //TODO Integrate Api when backend fixes the response.
                        if ((context as MainActivity).isOnline.value ?: false) {
                            NudgeLogger.d("TransectWalkScreen", "completeTolaAdditionClicked -> isOnline")
                            viewModel.addTolasToNetwork(object : NetworkCallbackListener {
                                override fun onSuccess() {
                                    NudgeLogger.d("TransectWalkScreen", "completeTolaAdditionClicked -> onSuccess")
                                    viewModel.callWorkFlowAPI(villageId, stepId, object : NetworkCallbackListener{
                                        override fun onSuccess() {

                                        }
                                        override fun onFailed() {
                                            NudgeLogger.d("TransectWalkScreen", "completeTolaAdditionClicked callWorkFlowAPI -> onFailed")
                                        }
                                    })
                                }
                                override fun onFailed() {
                                    NudgeLogger.d("TransectWalkScreen", "completeTolaAdditionClicked -> onFailed")
                                }

                            })

//                            viewModel.updateTolaNeedTOPostList(villageId)
                        }
                        viewModel.markTransectWalkComplete(villageId, stepId)
                        viewModel.updateWorkflowStatusInEvent(
                            stepStatus = StepStatus.COMPLETED,
                            villageId = villageId,
                            stepId = stepId
                        )
                        navController.navigate(
                            "step_completion_screen/${
                                context.getString(R.string.transect_walk_completed_message).replace(
                                    "{VILLAGE_NAME}",
                                    viewModel.villageEntity.value?.name ?: ""
                                )
                            }"
                        )

                    } else {
                        completeTolaAdditionClicked = true
                    }
                },
                negativeButtonOnClick = {/*Nothing to do here*/ }
            )
        } else {
            bottomPadding = 0.dp
        }
    }
}

@Composable
fun VillageDetailView(
    villageName: String,
    voName: String,
    stateId: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.padding(end = 16.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.home_icn),
                contentDescription = null,
                tint = textColorDark,
            )
            Text(
                text = " $villageName",
                modifier = Modifier
                    .fillMaxWidth(),
                color = textColorDark,
                style = smallTextStyle
            )
        }
        Row(
            modifier = Modifier
                .absolutePadding(left = 4.dp)
                .padding(end = 16.dp)
        ) {
            Text(
                text = getBengalString(LocalContext.current, stateId, R.plurals.vo),
                modifier = Modifier,
                color = textColorDark,
                style = smallTextStyle
            )
            Text(
                text = voName,
                modifier = Modifier
                    .fillMaxWidth(),
                color = textColorDark,
                style = smallTextStyle
            )
        }
    }
}
