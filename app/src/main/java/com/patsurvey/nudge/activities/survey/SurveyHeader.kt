package com.patsurvey.nudge.activities.survey

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.*
import com.patsurvey.nudge.customviews.CircularProgressBar
import com.patsurvey.nudge.database.VillageEntity
import com.patsurvey.nudge.model.dataModel.DidiDetailsModel

@Composable
fun SurveyHeader(
    modifier: Modifier,
    didiName: String,
    villageEntity: VillageEntity,
    questionCount: Int,
    answeredCount: Int,
    partNumber : Int
) {
    BoxWithConstraints {
        val constraintSet = surveyHeaderConstraints()
        ConstraintLayout(constraintSet, modifier = modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.home_icn),
                contentDescription = "home image",
                modifier = Modifier
                    .width(18.dp)
                    .height(14.dp)
                    .layoutId("homeImage"),
                colorFilter = ColorFilter.tint(textColorDark)
            )
            Text(
                text = villageEntity.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .layoutId("villageText"),
                color = textColorDark,
                style = smallTextStyle
            )


            Text(
                text = "VO: ${villageEntity.name} Mahila Mandal",
                modifier = Modifier
                    .fillMaxWidth()
                    .layoutId("voText"),
                color = textColorDark,
                style = smallTextStyle
            )

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
                style = largeTextStyle.copy(lineHeight = 27.sp)
            )

            CircularProgressBar(
                modifier = Modifier
                    .size(60.dp)
                    .layoutId("surveyProgress"),
                circleRadius = LocalDensity.current.run { 27.dp.toPx() },
                initialPosition = answeredCount,
                maxProgress = questionCount,
                borderThickness = 25.dp,
                centerTextSize = 15.sp
            )

            Text(
                text = stringResource(id = R.string.section_number, partNumber),
                modifier = Modifier
                    .layoutId("sectionText"),
                color = textColorDark,
                style = buttonTextStyle.copy(lineHeight = 22.sp)
            )

            Image(
                painter = painterResource(id = R.drawable.home_icn),
                contentDescription = "home image",
                modifier = Modifier
                    .width(66.dp)
                    .height(60.dp)
                    .layoutId("bigHomeImage"),
                colorFilter = ColorFilter.tint(textColorDark)
            )
        }
    }
}

private fun surveyHeaderConstraints(): ConstraintSet {
    return ConstraintSet {
        val homeImage = createRefFor("homeImage")
        val villageText = createRefFor("villageText")
        val voText = createRefFor("voText")
        val surveyText = createRefFor("surveyText")
        val didiNameText = createRefFor("didiNameText")
        val surveyProgress = createRefFor("surveyProgress")
        val sectionText = createRefFor("sectionText")
        val bigHomeImage = createRefFor("bigHomeImage")

        constrain(villageText) {
            start.linkTo(homeImage.end, 10.dp)
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
        }
        constrain(homeImage) {
            top.linkTo(villageText.top)
            bottom.linkTo(villageText.bottom)
            start.linkTo(parent.start)
        }
        constrain(voText) {
            start.linkTo(homeImage.start)
            top.linkTo(villageText.bottom)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
        }
        constrain(surveyText) {
            top.linkTo(voText.bottom, margin = 23.dp)
            start.linkTo(voText.start)
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

        val barrier = createBottomBarrier(didiNameText, surveyProgress, margin = 44.dp)

        constrain(sectionText) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(barrier)
        }

        constrain(bigHomeImage) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(sectionText.bottom, margin = 40.dp)
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
        villageEntity = VillageEntity(1, "Sundar Pahar", listOf()),
        questionCount = 6,
        answeredCount = 2,
        partNumber = 1

    )
}