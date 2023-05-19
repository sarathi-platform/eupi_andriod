package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.buttonTextStyle
import com.patsurvey.nudge.activities.ui.theme.textColorDark
import com.patsurvey.nudge.customviews.VOAndVillageBoxView

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PatSurvaySectionOneSummaryScreen(
    navController: NavHostController,
    modifier: Modifier,
    isOnline: Boolean = true,
    patDidiSummaryViewModel: PatDidiSummaryViewModel,
    didiDetails:String
) {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(modifier)
    ) {
        val (bottomActionBox, mainBox) = createRefs()
        Box(modifier = Modifier
            .constrainAs(mainBox) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }
            .padding(top = 14.dp)
        ) {

            Column(
                modifier = modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                VOAndVillageBoxView(
                    prefRepo = patDidiSummaryViewModel.prefRepo,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(id = R.string.pat_survey_section_1),
                    modifier = Modifier
                        .layoutId("sectionText"),
                    color = textColorDark,
                    style = buttonTextStyle.copy(lineHeight = 22.sp)
                )
                Text(
                    text = stringResource(id = R.string.result_summary),
                    modifier = Modifier
                        .layoutId("sectionText"),
                    color = textColorDark,
                    style = buttonTextStyle.copy(lineHeight = 22.sp)
                )
            }
        }
    }
}
