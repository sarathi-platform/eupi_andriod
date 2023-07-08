package com.patsurvey.nudge.activities.survey



import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.customviews.CircularProgressBar

@Composable
fun SurveyHeader(
    modifier: Modifier,
    didiName: String,
    questionCount: Int,
    answeredCount: Int,
    partNumber : Int,
    viewModel: QuestionScreenViewModel?=null
) {
    BoxWithConstraints {
        val constraintSet = surveyHeaderConstraints()
        ConstraintLayout(constraintSet, modifier = modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.pat_survey),
                modifier = Modifier
                    .fillMaxWidth()
                    .layoutId("surveyText"),
                color = textColorBlueLight,
                style = smallTextStyle.copy(lineHeight = 19.sp)
            )

            Text(
                text = "${stringResource(id = R.string.didi)}: ${didiName}",
                modifier = Modifier
                    .fillMaxWidth()
                    .layoutId("didiNameText"),
                color = textColorDark,
                style = TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            )
            CircularProgressBar(
                modifier = Modifier
                    .size(60.dp)
                    .layoutId("surveyProgress"),
                circleRadius = LocalDensity.current.run { 27.dp.toPx() },
                initialPosition = answeredCount.coerceAtMost(viewModel?.maxQuesCount?.value?:0),
                maxProgress = viewModel?.maxQuesCount?.value?:0,
                borderThickness = 25.dp,
                centerTextSize = 15.sp
            )

            Text(
                text = stringResource(id = R.string.section_number, partNumber),
                modifier = Modifier
                    .layoutId("sectionText"),
                color = textColorDark,
                style = TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            )
        }
    }
}

private fun surveyHeaderConstraints(): ConstraintSet {
    return ConstraintSet {
        val surveyText = createRefFor("surveyText")
        val didiNameText = createRefFor("didiNameText")
        val surveyProgress = createRefFor("surveyProgress")
        val sectionText = createRefFor("sectionText")


        constrain(surveyText) {
            top.linkTo(parent.top, margin = 10.dp)
            start.linkTo(parent.start)
            end.linkTo(surveyProgress.start, margin = 10.dp)
            width = Dimension.fillToConstraints
        }
        constrain(didiNameText) {
            top.linkTo(surveyText.bottom)
            start.linkTo(surveyText.start)
            end.linkTo(surveyProgress.start, margin = 10.dp)
            width = Dimension.fillToConstraints
        }

        constrain(surveyProgress) {
            top.linkTo(surveyText.top)
            end.linkTo(parent.end)
        }

        val barrier = createBottomBarrier(didiNameText, surveyProgress, margin = 20.dp)

        constrain(sectionText) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(barrier)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    SurveyHeader(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
        didiName = "Urmila Devi",
        questionCount = 6,
        answeredCount = 2,
        partNumber = 1

    )
}