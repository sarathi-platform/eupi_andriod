package com.patsurvey.nudge.activities.ui.transect_walk

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.customviews.CustomProgressBar
import com.patsurvey.nudge.customviews.ModuleAddedSuccessView
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        viewModel.isTransectWalkComplete(stepId)
    }
    var showAddTolaBox by remember { mutableStateOf(false) }
    val tolaList = viewModel.tolaList.filter { it.status == TolaStatus.TOLA_ACTIVE.ordinal }
    val tolaToBeEdited: Tola by remember { mutableStateOf(Tola()) }
    var completeTolaAdditionClicked by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val localDensity = LocalDensity.current

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    val focusManager = LocalFocusManager.current

    var bottomPadding by remember {
        mutableStateOf(0.dp)
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
            .then(modifier)
    ) {
        viewModel.setVillage(villageId)
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
                    .padding(horizontal = 16.dp)
            ) {
                VillageDetailView(
                    villageName = viewModel.villageEntity.value?.name ?: "",
                    voName = viewModel.villageEntity.value?.name ?: "",
                    modifier = Modifier
                )
                ModuleAddedSuccessView(completeAdditionClicked = completeTolaAdditionClicked,
                    message = stringResource(
                        R.string.tola_conirmation_text,
                        tolaList.filter { it.needsToPost && it.status == TolaStatus.TOLA_ACTIVE.ordinal }.size
                    ),
                    Modifier.padding(vertical = (screenHeight/4).dp)
                )

                LazyColumn(modifier = Modifier.padding(bottom = bottomPadding)) {

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
                                    if (!tolaList.contains(
                                            TolaEntity.createEmptyTolaForVillageId(
                                                villageId
                                            )
                                        )
                                    ) {
                                        ButtonOutline(
                                            modifier = Modifier.weight(0.9f).height(45.dp),
                                        ) {
                                            if (!showAddTolaBox)
                                                showAddTolaBox = true
                                        }
                                    }
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
                                                append("Showing")
                                            }
                                            withStyle(
                                                style = SpanStyle(
                                                    color = textColorDark,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontFamily = NotoSans
                                                )
                                            ) {
                                                append(" ${tolaList.size}")
                                            }
                                            withStyle(
                                                style = SpanStyle(
                                                    color = textColorDark,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Normal,
                                                    fontFamily = NotoSans
                                                )
                                            ) {
                                                append(" added Tolas")
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
                                                )
                                            )
                                            viewModel.markTransectWalkIncomplete(stepId,villageId)
                                            showAddTolaBox = false
                                            focusManager.clearFocus()
                                            showCustomToast(context,context.getString(R.string.tola_successfully_added).replace("{TOLA_NAME}", name))
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
                                        BlueButtonWithIcon(
                                            buttonText = stringResource(id = R.string.add_tola),
                                            icon = Icons.Default.Add,
                                            modifier = Modifier.padding(top = 16.dp)
                                        ) {
                                            if (!showAddTolaBox)
                                                showAddTolaBox = true
                                        }
                                        //TODO fix empty tola functionality
                                        /*EnptyTolaButton */
                                    }
                                }
                            }
                        } else {
                            itemsIndexed(tolaList) { index, tola ->
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
                                        deleteButtonClicked = {
                                            showCustomToast(context,context.getString(R.string.tola_deleted).replace("{TOLA_NAME}", tola.name))
                                            viewModel.removeTola(tola.id)
                                            viewModel.markTransectWalkIncomplete(stepId,villageId)
                                            showAddTolaBox = false
                                        },
                                        saveButtonClicked = { newName, newLocation ->
                                            showAddTolaBox = if (newName == tola.name && (newLocation?.lat == tola.latitude && newLocation.long == tola.longitude)) false
                                            else {
                                                viewModel.updateTola(tola.id, newName, newLocation)
                                                viewModel.markTransectWalkIncomplete(stepId,villageId)
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

        if (tolaList.isNotEmpty() && !viewModel.isTransectWalkComplete.value
            && viewModel.tolaList.filter { it.status == TolaStatus.TOLA_ACTIVE.ordinal }.any { it.needsToPost }) { //Check if we have to mark transect walk in progress if after completion a new tola is added?
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
                        //TODO Integrate Api when backend fixes the response.
                        if ((context as MainActivity).isOnline.value ?: false) {
                            viewModel.addTolasToNetwork()
                        }
                        viewModel.markTransectWalkComplete(villageId, stepId)
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
        }
    }
}

@Composable
fun VillageDetailView(
    villageName: String,
    voName: String,
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
                text = "VO: ",
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
