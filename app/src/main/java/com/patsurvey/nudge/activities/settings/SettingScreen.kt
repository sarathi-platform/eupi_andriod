package com.patsurvey.nudge.activities.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.black100Percent
import com.patsurvey.nudge.activities.ui.theme.borderGreyLight
import com.patsurvey.nudge.model.dataModel.SettingOptionModel
import com.patsurvey.nudge.navigation.ScreenRoutes
import com.patsurvey.nudge.navigation.home.SettingScreens
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.ButtonPositive
import com.patsurvey.nudge.utils.showCustomToast

@Composable
fun SettingScreen(
    viewModel: SettingViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
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
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(modifier)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .background(Color.White)
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
                    itemsIndexed(viewModel.optionList.value) { index, item ->
                        SettingCard(title = item.title, subTitle = item.subTitle) {
                            when (index) {
                                1 -> {
                                    navController.navigate(SettingScreens.PROFILE_SCREEN.route)
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
    onClick: () -> Unit
) {
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
            Text(
                text = title,
                textAlign = TextAlign.Start,
                fontSize = 16.sp,
                fontFamily = NotoSans,
                fontWeight = FontWeight.SemiBold,
                color = black100Percent,
                modifier = Modifier.fillMaxWidth()
            )

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
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.dp_2))
                .background(borderGreyLight)
        )
    }
}