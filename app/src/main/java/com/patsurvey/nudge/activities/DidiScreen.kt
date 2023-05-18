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
import com.patsurvey.nudge.utils.ARG_FROM_HOME
import com.patsurvey.nudge.utils.ARG_FROM_PAT_SURVEY
import com.patsurvey.nudge.utils.ARG_FROM_PROGRESS
import com.patsurvey.nudge.utils.BlueButtonWithDrawableIcon

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DidiScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    didiViewModel: AddDidiViewModel,
    villageId:Int,
    stepId:Int,
    onNavigateToAddDidi:()-> Unit
) {

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val fromPage = didiViewModel.prefRepo.getFromPage()
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
                        prefRepo = didiViewModel.prefRepo,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier.padding(vertical = (screenHeight/4).dp).align(
                                Alignment.TopCenter),
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
                                        append(stringResource(R.string.empty_didis_string))
                                    }
                                },
                                modifier = Modifier.padding(top = 32.dp)
                            )
                            Log.e("arg",didiViewModel.prefRepo.getFromPage())
                            if (!didiViewModel.prefRepo.getFromPage().equals(ARG_FROM_HOME, true)
                                && !didiViewModel.prefRepo.getFromPage().equals(ARG_FROM_PAT_SURVEY, true)){
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
        if (didiViewModel.didiList.value.isNotEmpty()) {
            SocialMappingDidiListScreen(
                navController,
                modifier = modifier,
                didiViewModel = didiViewModel,
                villageId = villageId,
                stepId = stepId
            )
        }
    }
    LaunchedEffect(key1 = Unit){
        didiViewModel.fetchDidisFrommDB()
    }
}