package com.patsurvey.nudge.activities.ui.vo_endorsement

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.customviews.CustomProgressBar
import com.patsurvey.nudge.customviews.VOAndVillageBoxView

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun VoEndorsementStepScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: VoEndorsementScreenViewModel,
    stepId: Int,
    isStepComplete:Boolean,
    onNavigateToSummary:()-> Unit
) {
    val openSummaryPage = remember {
        mutableStateOf(false)
    }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    if(viewModel.showLoader.value){
        CustomProgressBar(modifier = Modifier)
    }else {
        if (viewModel.didiList.value.isEmpty()) {
            ConstraintLayout(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxSize().padding(top = 16.dp)
                    .border(
                        width = 0.dp,
                        color = Color.Transparent,
                    )
            ) {
                Column(modifier = Modifier) {
                    VOAndVillageBoxView(
                        prefRepo = viewModel.repository.prefRepo,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .padding(vertical = (screenHeight / 4).dp)
                                .align(
                                    Alignment.TopCenter
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
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
                                        append(stringResource(R.string.no_didis_availble_for_vo_endorsement))
                                    }
                                },
                                modifier = Modifier.padding(top = 32.dp)
                            )
                        }
                    }
                }
            }
        }

        if(isStepComplete){
            if(!openSummaryPage.value)
                onNavigateToSummary()
            openSummaryPage.value=true
        }else{
            if(viewModel.didiList.value.isNotEmpty()){
                VoEndorsementScreen(viewModel = viewModel,
                    navController = navController,
                    modifier = modifier,
                    stepId = stepId)
            }
        }

    }
}
