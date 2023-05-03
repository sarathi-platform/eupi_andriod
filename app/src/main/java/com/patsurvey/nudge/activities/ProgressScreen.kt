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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.progress.ProgressScreenViewModel
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.navigation.ScreenRoutes
import com.patsurvey.nudge.utils.BlueButton
import com.patsurvey.nudge.utils.IconButtonForward
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

    val selectedVillage = viewModel.villageSelected.value
    var selectedText by remember { mutableStateOf(if (selectedVillage == -1) "Select Village" else viewModel.villageList.value[selectedVillage].villageName) }

    val steps by viewModel.stepList.collectAsState()
    val villages by viewModel.villageList.collectAsState()

    Log.d("PROGRESS_SCREEN", "viewModel.villageSelected.value: ${viewModel.villageSelected.value}")


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 14.dp)
            .then(modifier)
    ) {
        ModalBottomSheetLayout(
            sheetContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 26.dp)
                        .height(500.dp)
                ) {
                    Text(
                        text = "Select Village & VO",
                        style = smallTextStyle,
                        color = textColorDark,
                    )
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                        itemsIndexed(villages) { index, village ->
                            VillageAndVoBoxForBottomSheet(
                                tolaName = village.villageName,
                                voName = village.voName,
                                index = index,
                                selectedIndex = viewModel.villageSelected.value,
                            ) {
                                viewModel.villageSelected.value = it
                                selectedText = viewModel.villageList.value[it].villageName
                                scope.launch {
                                    scaffoldState.hide()
                                }
                            }
                        }
                    }

                }
            },
            sheetState = scaffoldState,
            sheetElevation = 20.dp,
            sheetBackgroundColor = Color.White,
            sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)
            ) {

                Column(
                    Modifier
                        .background(Color.White)
                ) {

                    ConstraintLayout() {
                        val (userDetail, moreMenu) = createRefs()
                        Column(
                            modifier = Modifier
                                .constrainAs(userDetail) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                }
                        ) {
                            Text(
                                text = "Akhilesh Negi",
                                color = textColorDark,
                                modifier = Modifier
                                    .padding(top = 20.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Start,
                                style = largeTextStyle
                            )
                            Text(
                                text = "ID: 234567",
                                color = textColorDark,
                                modifier = Modifier
                                    .padding(top = 6.dp, bottom = 16.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Start,
                                style = smallTextStyle
                            )
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.more_icon),
                            contentDescription = "more action button",
                            tint = blueDark,
                            modifier = Modifier
                                .constrainAs(moreMenu) {
                                    top.linkTo(userDetail.top, margin = 10.dp)
                                    end.linkTo(parent.end, margin = 10.dp)
                                }
                                .padding(20.dp)
                                .clickable {

                                }
                        )

                    }

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
                                scope.launch {
                                    if (!scaffoldState.isVisible) {
                                        scaffoldState.show()
                                    } else {
                                        scaffoldState.hide()
                                    }
                                }
                            },

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

                LazyColumn(
                    modifier = Modifier
                        .background(Color.White)
                ) {
                    itemsIndexed(items = steps) { index, step ->
                        StepsBox(
                            boxTitle = step.stepName,
                            stepNo = step.stepNo,
                            index = index,
                            shouldBeActive = (viewModel.stepSelected.value == index)
                        ) {
                            viewModel.stepSelected.value = it
                            when (it) {
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
                /*BlueButton(
                    buttonText = "Start Now",
                    isArrowRequired = true,
                    shouldBeActive = shouldBeActive,
                    modifier = Modifier
                        .padding(end = 14.dp)
                        .weight(0.8f),
                    onClick = {
                        onclick(index)
                    }
                )*/
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
                text = "Step $stepNo",
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