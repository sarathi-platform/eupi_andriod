package com.patsurvey.nudge.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.NotoSans
import com.patsurvey.nudge.activities.ui.theme.languageItemActiveBg

@Preview
@Composable
fun TolaScreen(
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(languageItemActiveBg)
    ) {
        val (bottomActionBox, mainBox) = createRefs()

        Box(modifier = Modifier
            .constrainAs(mainBox) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }
            .padding(top = 24.dp)
        ) {
            Column(modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Transact Walk",
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
//                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.padding(16.dp))
                    ButtonOutline(
                        modifier = Modifier.weight(1f)
                    ) {

                    }
                }
            }
        }

        DoubleButtonBox(
            modifier = Modifier
                .constrainAs(bottomActionBox) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                },
            positiveButtonText = stringResource(id = R.string.mark_complete_text),
            negativeButtonText = stringResource(id = R.string.go_back_text),
            positiveButtonOnClick = {

            },
            negativeButtonOnClick = {

            }
        )
    }
}