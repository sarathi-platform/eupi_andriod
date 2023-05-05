package com.patsurvey.nudge.activities

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.progress.ProgressScreenViewModel
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.navigation.ScreenRoutes
import com.patsurvey.nudge.utils.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProgressScreen(
    modifier: Modifier = Modifier,
    viewModel: ProgressScreenViewModel,
    stepsNavHostController: NavHostController,
) {

    val scaffoldState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = false)
    val scope = rememberCoroutineScope()

    val steps by viewModel.stepList.collectAsState()
    val villages by viewModel.villageList.collectAsState()

    val mainActivity = LocalContext.current as? MainActivity
    mainActivity?.isLoggedInLive?.postValue(viewModel.isLoggedIn())

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    val selectedText = remember { mutableStateOf("Select Village") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        ModalBottomSheetLayout(
            sheetContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                        .height((screenHeight/2).dp)
                ) {
                    Text(
                        text = "Select Village & VO",
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = textColorDark,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                        itemsIndexed(villages) { index, village ->
                            VillageAndVoBoxForBottomSheet(
                                tolaName = village.name,
                                voName = village.name,
                                index = index,
                                selectedIndex = viewModel.villageSelected.value,
                            ) {
                                viewModel.villageSelected.value = it
                                selectedText.value = viewModel.villageList.value[it].name
                                scope.launch {
                                    scaffoldState.hide()
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }

                }
            },
            sheetState = scaffoldState,
            sheetElevation = 20.dp,
            sheetBackgroundColor = Color.White,
            sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        ) {
            Scaffold(
                modifier = Modifier,
                topBar = {
                    ProgressScreenTopBar() {

                    }
                }
            ) { it ->
                if (viewModel.showLoader.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(top = it.calculateTopPadding() + 30.dp)
                    ) {
                        CircularProgressIndicator(
                            color = blueDark,
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.Center)
                        )
                    }
                } else {
                    LazyColumn(
                        Modifier
                            .background(Color.White)
                            .padding(start = 16.dp, end = 16.dp, top = it.calculateTopPadding()),
//                        verticalArrangement = Arrangement.
                    ) {

                        item {
                            UserDataView(
                                modifier = Modifier,
                                name = viewModel.prefRepo.getPref(
                                    PREF_KEY_NAME,
                                    BLANK_STRING
                                ) ?: "",
                                identity = viewModel.prefRepo.getPref(
                                    PREF_KEY_IDENTITY_NUMBER,
                                    BLANK_STRING
                                ) ?: ""
                            )
                        }

                        item {
                           selectedText.value = villages[viewModel.villageSelected.value].name
                            VillageSelectorDropDown(selectedText = selectedText.value) {
                                scope.launch {
                                    if (!scaffoldState.isVisible) {
                                        scaffoldState.show()
                                    } else {
                                        scaffoldState.hide()
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        itemsIndexed(items = steps.sortedBy { it.orderNumber }) { index, step ->
                            if ((viewModel.prefRepo.getPref(PREF_PROGRAM_NAME, "")?: "").equals("CRP Program", true) && index < 5) {
                                StepsBox(
                                    boxTitle = step.name,
                                    stepNo = step.orderNumber,
                                    index = index,
                                    shouldBeActive = (viewModel.stepSelected.value == index)
                                ) { index ->
                                    viewModel.stepSelected.value = index
                                    when (index) {
                                        0 -> {
                                            stepsNavHostController.navigate(ScreenRoutes.TRANSECT_WALK_SCREEN.route)
                                        }
                                        1 -> {}
                                        2 -> {}
                                        3 -> {}
                                        4 -> {}
                                        5 -> {}
                                    }

                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun StepsBox(
    modifier: Modifier = Modifier,
    boxTitle: String,
    stepNo: Int,
    index: Int,
    isCompleted: Boolean = false,
    shouldBeActive: Boolean = false,
    onclick: (Int) -> Unit
) {
    val dividerMargins = 32.dp
    if (stepNo == 6)
        Spacer(modifier = Modifier.height(20.dp))

    ConstraintLayout(
        modifier = Modifier
            .background(Color.White)
            .border(
                width = 0.dp,
                color = Color.Transparent,
            )
            .then(modifier)
    ) {
        val (step_no, stepBox, divider1, divider2) = createRefs()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = 1.dp,
                    color = greyBorder,
                    shape = RoundedCornerShape(6.dp)
                )
                .background(Color.White)
                .constrainAs(stepBox) {
                    start.linkTo(parent.start)
                    top.linkTo(step_no.bottom, margin = -16.dp)
                }

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isCompleted) green else if (shouldBeActive) stepBoxActiveColor else white)
                    .padding(top = 14.dp, bottom = 14.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(
                    modifier = Modifier
                        .absolutePadding(left = 10.dp)
                        .weight(1.2f)
                ) {
                    Text(
                        text = boxTitle/* "Transect Walk"*/,
                        color = textColorDark,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 16.dp, end = 48.dp, bottom = 16.dp),
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = mediumTextStyle
                    )
//                    Row(
//                        modifier = Modifier
//                            .padding(horizontal = 16.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Canvas(
//                            modifier = Modifier
//                                .size(size = 10.dp)
//                        ) {
//                            drawCircle(
//                                color = if (isCompleted) greenDark else greyIndicator,
//                            )
//                        }
//                        Text(
//                            text = if (isCompleted) "Completed" else "Not Started",
//                            color = if (isCompleted) greenDark else textColorDark,
//                            style = smallerTextStyle,
//                            modifier = Modifier.padding(start = 6.dp, bottom = 4.dp)
//
//                        )
//                    }
                    if (isCompleted)
                        Spacer(modifier = Modifier.height(20.dp))
                }

                if (shouldBeActive) {
                    IconButtonForward(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(0.2f)
                    ) {
                        onclick(index)
                    }
                }
            }
        }


        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = 1.dp,
                    color = greyBorder,
                    shape = RoundedCornerShape(100.dp)
                )
                .background(Color.White)
                .padding(6.dp)
                .constrainAs(step_no) {
                    start.linkTo(parent.start, margin = 16.dp)
                    top.linkTo(parent.top)
                }
        ) {
            Text(
                text = "$stepNo",
                color = textColorDark,
                style = smallerTextStyleNormalWeight,
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 16.dp)

            )
        }

        if (stepNo < 5) {
            Divider(
                color = greyBorder,
                modifier = Modifier
                    .height(10.dp)  //fill the max height
                    .width(1.dp)
                    .constrainAs(divider1) {
                        start.linkTo(parent.start, margin = dividerMargins)
                        top.linkTo(stepBox.bottom)
                    }
                    .padding(vertical = 2.dp)
            )

            Divider(
                color = greyBorder,
                modifier = Modifier
                    .height(10.dp)  //fill the max height
                    .width(1.dp)
                    .constrainAs(divider2) {
                        start.linkTo(parent.start, margin = dividerMargins)
                        top.linkTo(divider1.bottom)
                    }
                    .padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
fun UserDataView(
    modifier: Modifier = Modifier,
    name: String,
    identity: String
) {
    ConstraintLayout() {
        val (userDetail, moreMenu) = createRefs()
        Column(
            modifier = Modifier
                .constrainAs(userDetail) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .then(modifier)
        ) {
            Text(
                text = name,
                color = textColorDark,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Start,
                style = largeTextStyle
            )

            Text(
                text = stringResource(R.string.user_id_text) + identity,
                color = textColorDark,
                modifier = Modifier
                    .padding(top = 6.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Start,
                style = smallTextStyle
            )

        }
    }
}

@Composable
fun VillageSelectorDropDown(
    modifier: Modifier = Modifier,
    selectedText: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(dropDownBg)
            .clip(RoundedCornerShape(6.dp))
            .height(56.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = Color.Black
                )
            ) {
                onClick()

            }
            .then(modifier),
        ) {
        Row(
            Modifier
                .padding(14.dp)
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedText,
                color = blueDark,
            )
            Icon(
                painterResource(id = R.drawable.baseline_keyboard_arrow_down),
                contentDescription = "drop down menu icon",
                tint = blueDark
            )
        }

    }
}

@Composable
fun ProgressScreenTopBar(
    modifier: Modifier = Modifier,
    onHamburgerClick: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .then(modifier)
    ) {
        ConstraintLayout(
            Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(48.dp)
                .align(Alignment.Center),
        ) {
            val (titleItem, moreMenu) = createRefs()
            Text(
                text = "Sarathi",
                color = textColorDark,
                fontFamily = NotoSans,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .constrainAs(titleItem)
                    {
                        top.linkTo(parent.top, margin = 8.dp)
                        start.linkTo(parent.start)
                    }
            )

            Icon(
                painter = painterResource(id = R.drawable.more_icon),
                contentDescription = "more action button",
                tint = blueDark,
                modifier = Modifier
                    .constrainAs(moreMenu) {
                        top.linkTo(titleItem.top)
                        end.linkTo(parent.end)
                    }
                    .padding(10.dp)
                    .clickable {
                        onHamburgerClick()
                    }
            )
        }
    }
}