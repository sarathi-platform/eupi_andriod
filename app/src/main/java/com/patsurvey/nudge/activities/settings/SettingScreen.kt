package com.patsurvey.nudge.activities.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.model.dataModel.SettingOptionModel
import com.patsurvey.nudge.navigation.home.HomeScreens
import com.patsurvey.nudge.navigation.home.SettingScreens
import com.patsurvey.nudge.navigation.navgraph.Graph
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.ButtonPositive
import com.patsurvey.nudge.utils.EXPANSTION_TRANSITION_DURATION
import com.patsurvey.nudge.utils.showCustomToast

@Composable
fun SettingScreen(
    viewModel: SettingViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
//    LaunchedEffect(key1 = true) {
    val list = ArrayList<SettingOptionModel>()
    list.add(
        SettingOptionModel(
            1,
            context.getString(R.string.sync_up),
            context.getString(R.string.last_syncup_text)
        )
    )
    list.add(SettingOptionModel(2, context.getString(R.string.profile), BLANK_STRING))
    list.add(SettingOptionModel(3, context.getString(R.string.forms), BLANK_STRING))
    list.add(SettingOptionModel(4, context.getString(R.string.training_videos), BLANK_STRING))
    list.add(SettingOptionModel(5, context.getString(R.string.language_text), BLANK_STRING))
    viewModel.createSettingMenu(list)
//    }
    LaunchedEffect(key1 = true) {
        val villageId = viewModel.prefRepo.getSelectedVillage().id
        viewModel.isFormAAvailableForVillage(villageId)
        viewModel.isFormBAvailableForVillage(villageId)
        viewModel.isFormCAvailableForVillage(villageId)
    }

    val formList = mutableListOf<String>()
//    if (!viewModel.prefRepo.getPref(PREF_WEALTH_RANKING_COMPLETION_DATE, "").isNullOrEmpty() || viewModel.formAAvailabe.value) {
    formList.add("Digital Form A")
//    }
//    if (!viewModel.prefRepo.getPref(PREF_PAT_COMPLETION_DATE, "").isNullOrEmpty() || viewModel.formBAvailabe.value){
    formList.add("Digital Form B")
//    }
//    if (!viewModel.prefRepo.getPref(PREF_VO_ENDORSEMENT_COMPLETION_DATE, "").isNullOrEmpty() || viewModel.formCAvailabe.value) {
    formList.add("Digital Form C")
//    }

    val optionList = viewModel.optionList.collectAsState()

    val expanded = remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(modifier),
        topBar = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "back press",
                    tint = textColorDark,
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 16.dp)
                        .clickable {
                        navController.navigate(Graph.HOME) {
                            popUpTo(HomeScreens.PROGRESS_SCREEN.route) {
                                inclusive = true
                            }
                        }
                    }
                )
                Text(text = "Settings", style = mediumTextStyle, color = textColorDark, modifier = Modifier.padding(vertical = 10.dp).weight(1f), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.size(24.dp).padding(vertical = 10.dp))
            }
        }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .background(Color.White)
                .padding(top = it.calculateTopPadding())
                .fillMaxSize()
        ) {
            val (mainBox, logoutButton) = createRefs()

            Column(modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .constrainAs(mainBox) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }) {
                LazyColumn {
                    itemsIndexed(optionList.value) { index, item ->
                        SettingCard(
                            title = item.title,
                            subTitle = item.subTitle,
                            expanded = item.title == stringResource(id = R.string.forms) && expanded.value,
                            showArrow = item.title == stringResource(id = R.string.forms),
                            formList = formList,
                            navController = navController
                        ) {
                            when (index) {
                                0 -> {
                                    viewModel.syncDataOnServer(context)
                                }
                                1 -> {
                                    navController.navigate(SettingScreens.PROFILE_SCREEN.route)
                                }
                                2 -> {
                                    expanded.value = !expanded.value
                                }
                                3 -> {
                                    navController.navigate(SettingScreens.VIDEO_LIST_SCREEN.route)
                                }
                                4 -> {
                                    navController.navigate(SettingScreens.LANGUAGE_SCREEN.route)
                                }

                                else -> {
                                    showCustomToast(
                                        context,
                                        context.getString(R.string.this_section_is_in_progress)
                                    )
                                }
                            }
                        }
                    }
                }

            }

            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.dp_15))
                .padding(vertical = dimensionResource(id = R.dimen.dp_15))
                .constrainAs(logoutButton) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }) {
                ButtonPositive(
                    buttonTitle = stringResource(id = R.string.logout),
                    isArrowRequired = false,
                    isActive = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)

                ) {
                }
            }

        }

    }
}

@Composable
fun SettingCard(
    title: String,
    subTitle: String,
    expanded: Boolean,
    showArrow: Boolean = false,
    formList: List<String>,
    navController: NavController,
    onClick: () -> Unit
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

    Column(modifier = Modifier
        .background(Color.White)
        .fillMaxWidth()
        .clickable {
            onClick()
        }) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.dp_20))
                .padding(vertical = dimensionResource(id = R.dimen.dp_15))
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = title,
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    color = textColorDark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                if (showArrow) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down_24),
                        contentDescription = null,
                        tint = textColorDark,
                        modifier = Modifier.rotate(arrowRotationDegree)
                    )
                }
            }
            if (!subTitle.isNullOrEmpty()) {
                Text(
                    text = subTitle,
                    textAlign = TextAlign.Start,
                    fontSize = 13.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.Normal,
                    color = black100Percent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        ExpandedSettingsList(
            modifier = Modifier,
            expanded = animateInt == 1,
            formList = formList,
            navController = navController
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.dp_2))
                .background(borderGreyLight)
        )

    }
}

@Composable
fun ExpandedSettingsList(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    formList: List<String>,
    navController: NavController
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

    val interactionSource = remember { MutableInteractionSource() }

    AnimatedVisibility(
        visible = expanded,
        enter = enterTransition,
        exit = exitTransition,
        modifier = Modifier.then(modifier)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
        ) {
            if (formList.isNotEmpty()) {
                formList.forEachIndexed { index, name ->

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                        Text(
                            text = name,
                            textAlign = TextAlign.Start,
                            fontSize = 14.sp,
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            color = textColorDark,
                            modifier = Modifier
                                .padding(horizontal = 26.dp)
                                .padding(top = if (index == 0) 0.dp else 8.dp, bottom = 8.dp)
                                .fillMaxWidth()
                                .indication(
                                    interactionSource = interactionSource,
                                    indication = rememberRipple(
                                        bounded = true,
                                        color = Color.Black
                                    )
                                )
                                .clickable {
                                    when (index) {
                                        0 -> navController.navigate(SettingScreens.FORM_A_SCREEN.route)
                                        1 -> {}/*navController.navigate(SettingScreens.FORM_B_SCREEN.route)*/
                                        2 -> {
                                            //add Form C action once done.
                                        }
                                    }

                                })

                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down_24),
                            contentDescription = null,
                            tint = textColorDark,
                            modifier = Modifier.rotate(-90f)
                        )
                    }
                    if (index < formList.size - 1)
                        Divider(
                            color = borderGreyLight,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 26.dp)
                        )
                }
            } else {
                Text(
                    text = "No Form available yet.",
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp,
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    color = textColorDark,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }

}
