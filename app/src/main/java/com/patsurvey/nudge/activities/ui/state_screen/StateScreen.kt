package com.patsurvey.nudge.activities.ui.state_screen

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.ui.common_components.ToolbarComponent
import com.nrlm.baselinesurvey.ui.theme.dimen_32_dp
import com.nrlm.baselinesurvey.ui.theme.white
import com.nudge.core.database.entities.state.StateEntity
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBorderBg
import com.patsurvey.nudge.activities.ui.theme.languageItemInActiveBorderBg
import com.patsurvey.nudge.activities.ui.theme.textColorBlueLight
import com.patsurvey.nudge.customviews.SarathiLogoTextView
import com.patsurvey.nudge.navigation.ScreenRoutes
import com.patsurvey.nudge.utils.ARG_FROM_HOME
import com.patsurvey.nudge.utils.BLANK_STRING
import com.patsurvey.nudge.utils.NudgeLogger
import com.patsurvey.nudge.utils.showCustomToast

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun StateScreen(
    navController: NavController,
    viewModel: StateScreenViewModel,
    modifier: Modifier,
    pageFrom: String
) {
    val context = LocalContext.current
    HandleNetworkError(viewModel, context)

    HandleBackPress(pageFrom, viewModel, navController, context)

    Scaffold(
        backgroundColor = white,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (pageFrom != ARG_FROM_HOME) {
                ToolbarComponent(
                    title = stringResource(R.string.language_text),
                    modifier = Modifier
                ) {
                    navController.navigateUp()

                }
            }
        },
        bottomBar = {

        }) {
        Box(
            modifier = Modifier
                .background(color = Color.White)
                .fillMaxSize()
                .padding(
                    top = it.calculateTopPadding() + dimen_32_dp,
                    start = dimensionResource(id = R.dimen.padding_16dp),
                    end = dimensionResource(id = R.dimen.padding_16dp),
                    bottom = dimensionResource(id = R.dimen.padding_32dp)
                )
                .then(modifier)
        ) {
            Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                if (pageFrom == ARG_FROM_HOME) {
                    SarathiLogoTextView()
                    Text(
                        text = "Choose State",
                        color = textColorBlueLight,
                        fontSize = 18.sp,
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.dp_20))
                    )
                }
                LanguageList(viewModel, context)
            }
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                ContinueButton(viewModel, pageFrom, navController, context)
            }
        }

    }

}

@Composable
fun LanguageItem(
    stateModel: StateEntity,
    index: Int,
    selectedIndex: Int,
    onClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 0.dp)
            .height(dimensionResource(id = R.dimen.height_60dp))
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = 1.dp,
                color = if (index == selectedIndex) languageItemActiveBorderBg else languageItemInActiveBorderBg,
                shape = RoundedCornerShape(6.dp)
            )
            .background(if (index == selectedIndex) languageItemActiveBg else Color.White)
            .clickable {
                onClick(index)
            }
    ) {
        Text(
            text = stateModel.localName ?: stateModel.state,
            color = blueDark,
            fontSize = 18.sp,
            fontFamily = NotoSans,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun LanguageList(viewModel: StateScreenViewModel, context: Context) {
    Column {
        viewModel.stateList.value?.let {
            LazyColumn {
                itemsIndexed(it) { index, item ->
                    LanguageItem(
                        stateModel = item,
                        index,
                        viewModel.statePosition.value
                    ) { i ->
                        viewModel.statePosition.value = i
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
fun ContinueButton(
    viewModel: StateScreenViewModel,
    pageFrom: String,
    navController: NavController,
    context: Context
) {
    Button(
        onClick = {
            try {
                viewModel.stateList.value?.get(viewModel.statePosition.value)?.let {
                    it.id?.let { stateId ->
                        if (stateId == 1) {
                            viewModel.setBaseUrl("https://uat.eupi-sarthi.in/")
                        } else {
                            viewModel.setBaseUrl("https://sarathi.lokos.in/")
                        }
                    }
                }
                navController.navigate(ScreenRoutes.LOGIN_SCREEN.route)
            } catch (ex: Exception) {
                NudgeLogger.e("StateScreen", "Continue Button click", ex)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        colors = ButtonDefaults.buttonColors(blueDark),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = stringResource(id = R.string.continue_text),
            color = Color.White,
            fontSize = 18.sp,
            fontFamily = NotoSans,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .background(blueDark)
        )
    }
}

@Composable
fun HandleNetworkError(viewModel: StateScreenViewModel, context: Context) {
    val networkErrorMessage = viewModel.networkErrorMessage.value
    if (networkErrorMessage.isNotEmpty()) {
        showCustomToast(context, networkErrorMessage)
        viewModel.networkErrorMessage.value = BLANK_STRING
    }
}

@Composable
fun HandleBackPress(
    pageFrom: String,
    viewModel: StateScreenViewModel,
    navController: NavController,
    context: Context
) {
    BackHandler {
        navController.popBackStack()
    }
}


