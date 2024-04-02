package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.dimen_0_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.summaryCardViewBlue
import com.nrlm.baselinesurvey.ui.theme.white

@Composable
fun SummaryCardComponent(
    itemCount: Int,
    question: QuestionEntity?,
    onViewSummaryClicked: (questionId: Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimen_10_dp),
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(weight_10_percent)
//        )
        Card(
            shape = RoundedCornerShape(roundedCornerRadiusDefault),
            elevation = CardDefaults.cardElevation(defaultElevation = dimen_0_dp),
            colors = CardDefaults.cardColors(containerColor = white),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimen_16_dp)
//                .weight(weight_80_percent)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimen_8_dp)
                )
                /*Text(
                    text = "Summary", //TODO Remove Hard coded strings
                    color = blueDark,
                    style = TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimen_8_dp)
                )*/
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        ) {
                            append("Total added: ")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        ) {
                            append("$itemCount")
                        }
                    },
                    color = blueDark,
                    style = TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimen_10_dp)
                )
                LinkTextButtonWithIcon(
                    title = "View Summary",
                    textColor = summaryCardViewBlue,
                    iconTint = summaryCardViewBlue
                ) {
                    onViewSummaryClicked(question?.questionId!!)
                }
//                CTAButtonComponent60PercentWidth(
//                    tittle = "View Summary"
//                ) {
//                    onViewSummaryClicked(question?.questionId!!)
//                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimen_8_dp)
                )
            }

        }
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(weight_10_percent)
//        )
    }
}