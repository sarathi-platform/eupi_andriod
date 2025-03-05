package com.patsurvey.nudge.activities.ui.selectlanguage

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nrlm.baselinesurvey.ui.common_components.ToolbarComponent
import com.nrlm.baselinesurvey.ui.theme.dimen_32_dp
import com.nrlm.baselinesurvey.ui.theme.white
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.mediumRankColor
import com.patsurvey.nudge.customviews.SarathiLogoTextViewV2
import com.patsurvey.nudge.utils.ARG_FROM_HOME

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun LanguageScreenV2(
    viewModel: LanguageViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
    pageFrom: String
) {
    val context = LocalContext.current
    HandleNetworkError(viewModel, context)
    if (pageFrom == ARG_FROM_HOME) {
        RequestPermissions()
    }

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
                .fillMaxSize()
                .background(mediumRankColor)
        ) {
            Image(
                painter = painterResource(id = R.drawable.lokos_bg),
                contentDescription = "Background Image",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
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
                        SarathiLogoTextViewV2()
                        Text(
                            text = stringResource(id = R.string.choose_language),
                            color = blueDark,
                            fontSize = 16.sp,
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.dp_20))
                        )
                    }
                    LanguageList(viewModel, context)
                }
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    ContinueButton(pageFrom, viewModel, navController, context)
                }
            }
        }


    }

    LaunchedEffect(Unit) {
        viewModel.languageList.value?.mapIndexed { index, languageEntity ->
            if (languageEntity.langCode.equals(
                    viewModel.languageRepository.prefRepo.getAppLanguage(),
                    true
                )
            ) {
                viewModel.languagePosition.value = index
            }
        }
    }
}






