package com.patsurvey.nudge.activities

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nudge.core.KOKBOROK_LANGUAGE_CODE
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.bpc.bpc_village_screen.BpcVillageSelectionScreen
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.utils.ButtonPositive
import com.patsurvey.nudge.utils.NudgeCore
import com.patsurvey.nudge.utils.PageFrom

@Composable
fun VillageScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    navController: NavController,
    viewModel: VillageScreenViewModel = hiltViewModel(),
    onNavigateToSetting:()->Unit
) {
    if(viewModel.prefRepo.getAppLanguage()== KOKBOROK_LANGUAGE_CODE){
        LanguageNotAvailableScreen(viewModel = viewModel) {
            viewModel.prefRepo.saveSettingOpenFrom(PageFrom.VILLAGE_PAGE.ordinal)
            onNavigateToSetting()
        }
    }else{
        if (viewModel.isUserBpc()) {
            BpcVillageSelectionScreen(
                navController = navController,
            ) {
                onNavigateToSetting()
            }
        } else {
            VillageSelectionScreen(navController = navController, viewModel = hiltViewModel()) {
                onNavigateToSetting()
            }
        }
    }



}

@Composable
fun LanguageNotAvailableScreen(
    viewModel: VillageScreenViewModel? = null,
    onNavigateToSetting: () -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = NudgeCore.getVoNameForState(
                            context, viewModel?.getStateId() ?: 4,
                            R.plurals.seletc_village_screen_text
                        ),
                        fontFamily = NotoSans,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp, color = textColorDark,
                        modifier = Modifier.fillMaxWidth()
                    )

                },
                actions = {
                },
                backgroundColor = Color.White,
                elevation = 10.dp
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp + it.calculateTopPadding())
        ) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    text = stringResource(id = R.string.this_language_is_not_available_for_selection),
                    fontFamily = NotoSans,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp, color = textColorDark,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))
                ButtonPositive(
                    buttonTitle = stringResource(id = R.string.change_language),
                    isArrowRequired = false,
                    modifier = Modifier.padding()
                ) {
                    onNavigateToSetting()
                }
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageNotAvailableScreenPreview(){
    LanguageNotAvailableScreen(){}
}
