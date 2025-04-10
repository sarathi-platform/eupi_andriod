package com.patsurvey.nudge.activities
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.blueDark
import com.patsurvey.nudge.activities.ui.theme.largeTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.customviews.CustomProgressBar
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.utils.ARG_FROM_HOME
import com.patsurvey.nudge.utils.ARG_FROM_PAT_SURVEY
import com.patsurvey.nudge.utils.BlueButtonWithDrawableIcon

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DidiScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    didiViewModel: AddDidiViewModel,
    villageId:Int,
    stepId:Int,
    onNavigateToAddDidi:()-> Unit,
    onNavigateToSummary:()-> Unit
) {

    LaunchedEffect(key1 = true) {
        didiViewModel.checkIfTolaIsNotDeleted()
        didiViewModel.saveStepId(stepId)
        didiViewModel.isSocialMappingComplete(stepId)
    }
    val openSummaryPage = remember {
        mutableStateOf(false)
    }

    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenHeight = configuration.screenHeightDp
    if(didiViewModel.showLoader.value){
        CustomProgressBar(modifier = Modifier)
    }else {
        if (didiViewModel.didiList.value.isEmpty()) {
            ConstraintLayout(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxSize()
                    .border(
                        width = 0.dp,
                        color = Color.Transparent,
                    )
            ) {
                Column(modifier = Modifier) {
                    VOAndVillageBoxView(
                        prefRepo = didiViewModel.addDidiRepository.prefRepo,
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
                            Text(text = stringResource(id = R.string.social_mapping),
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
                                        append(stringResource(R.string.empty_didis_string))
                                    }
                                },
                                modifier = Modifier.padding(top = 32.dp)
                            )
                            Log.d("arg", didiViewModel.getFromPage())
                            if (!didiViewModel.getFromPage().equals(ARG_FROM_HOME, true)
                                && !didiViewModel.getFromPage().equals(ARG_FROM_PAT_SURVEY, true)
                            ) {
                                BlueButtonWithDrawableIcon(
                                    buttonText = stringResource(id = R.string.add_didi),
                                    icon = Icons.Default.Add,
                                    imageIcon = R.drawable.didi_icon,
                                    modifier = Modifier.padding(top = 16.dp)
                                ) {
                                    didiViewModel.resetAllFields()
                                    onNavigateToAddDidi()
                                }
                            }
                        }
                    }
                }
            }
        }


        if (didiViewModel.getFromPage().equals(ARG_FROM_PAT_SURVEY, true)) {
            if (didiViewModel.isPATSurveyComplete.value) {
                if (!openSummaryPage.value) {
                    (context as MainActivity).isBackFromSummary.value = false
                    onNavigateToSummary()
                }
                openSummaryPage.value = true
            } else {
                SocialMappingDidiListScreen(
                    navController,
                    modifier = modifier,
                        didiViewModel = didiViewModel,
                        villageId = villageId,
                        stepId = stepId
                    )
                }
            }else{
                if(didiViewModel.didiList.value.isNotEmpty()) {
                    SocialMappingDidiListScreen(
                        navController,
                        modifier = modifier,
                        didiViewModel = didiViewModel,
                        villageId = villageId,
                        stepId = stepId
                    )
                }
            }

    }
    LaunchedEffect(key1 = Unit){
        didiViewModel.fetchDidisFrommDB()
    }
}