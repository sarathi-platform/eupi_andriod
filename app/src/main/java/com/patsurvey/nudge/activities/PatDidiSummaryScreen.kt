package com.patsurvey.nudge.activities

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.theme.borderGreyLight
import com.patsurvey.nudge.activities.ui.theme.didiDetailItemStyle
import com.patsurvey.nudge.activities.ui.theme.didiDetailLabelStyle
import com.patsurvey.nudge.customviews.VOAndVillageBoxView
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.utils.BLANK_STRING


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DidiSummaryScreen(navController: NavHostController, modifier: Modifier,
                      isOnline: Boolean = true, patDidiSummaryViewModel: PatDidiSummaryViewModel, didiDetails:String, onNavigation:()->Unit){

    val didi= Gson().fromJson(didiDetails, DidiEntity::class.java)

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                ) {
                    MainTitle(stringResource(R.string.pat_survey_title), Modifier.weight(1f))
                }
                ConstraintLayout(patDidiDetailConstraints(), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.house_number) + ":",
                        style = didiDetailLabelStyle,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.layoutId("houseNumberLabel")
                    )

                    Text(
                        text = didi.address,
                        style = didiDetailItemStyle,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.layoutId("houseNumber")
                    )

                    Text(
                        text = stringResource(id = R.string.didi) + ":",
                        style = didiDetailLabelStyle,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.layoutId("latestStatusLabel")
                    )

                    Text(
                        text = didi.name,
                        style = didiDetailItemStyle,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.layoutId("latestStatus")
                    )
                    Text(
                        text = stringResource(id = R.string.dada) + ":",
                        style = didiDetailLabelStyle,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.layoutId("dadaNameLabel")
                    )

                    Text(
                        text = didi.guardianName,
                        style = didiDetailItemStyle,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.layoutId("dadaName")
                    )
                    Text(
                        text = stringResource(id = R.string.tola) + ":",
                        style = didiDetailLabelStyle,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.layoutId("tolaLabel")
                    )

                    Text(
                        text = didi.cohortName,
                        style = didiDetailItemStyle,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.layoutId("tola")
                    )

                    Text(
                        text = stringResource(id = R.string.caste) + ":",
                        style = didiDetailLabelStyle,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.layoutId("casteLabel")
                    )

                    Text(
                        text = didi.castName ?: BLANK_STRING,
                        style = didiDetailItemStyle,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.layoutId("caste")
                    )


                    Spacer(
                        modifier = Modifier
                            .layoutId("bottomPadding")
                            .height(30.dp)
                    )
                }

                }
        }
    }

}

private fun patDidiDetailConstraints(): ConstraintSet {
    return ConstraintSet {
        val divider = createRefFor("divider")
        val houseNumberLabel = createRefFor("houseNumberLabel")
        val houseNumber = createRefFor("houseNumber")
        val dadaNameLabel = createRefFor("dadaNameLabel")
        val dadaName = createRefFor("dadaName")
        val casteLabel = createRefFor("casteLabel")
        val caste = createRefFor("caste")
        val tolaLabel = createRefFor("tolaLabel")
        val tola = createRefFor("tola")
        val latestStatusLabel = createRefFor("latestStatusLabel")
        val latestStatus = createRefFor("latestStatus")
        val bottomPadding = createRefFor("bottomPadding")

//        val centerGuideline = createGuidelineFromStart(0.5f)


        constrain(divider) {
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            start.linkTo(parent.start)
        }

        constrain(houseNumberLabel) {
            start.linkTo(parent.start, margin = 15.dp)
            top.linkTo(divider.bottom, margin = 15.dp)
//            end.linkTo(centerGuideline)
//            width = Dimension.fillToConstraints
        }

        constrain(houseNumber) {
            start.linkTo(houseNumberLabel.end, margin = 10.dp)
            top.linkTo(houseNumberLabel.top)
            bottom.linkTo(houseNumberLabel.bottom)
//            end.linkTo(parent.end, margin = 10.dp)
//            width = Dimension.fillToConstraints
        }

        constrain(latestStatusLabel) {
            start.linkTo(houseNumberLabel.start)
            top.linkTo(houseNumberLabel.bottom, margin = 20.dp)
//            end.linkTo(centerGuideline)
//            width = Dimension.fillToConstraints
        }

        constrain(dadaName) {
            start.linkTo(dadaNameLabel.end, margin = 10.dp)
            top.linkTo(latestStatusLabel.top)
            bottom.linkTo(latestStatusLabel.bottom)
//            end.linkTo(parent.end, margin = 10.dp)
//            width = Dimension.fillToConstraints
        }
        constrain(casteLabel) {
            start.linkTo(houseNumberLabel.start)
            top.linkTo(tolaLabel.bottom, margin = 15.dp)
//            end.linkTo(centerGuideline)
//            width = Dimension.fillToConstraints
        }

        constrain(latestStatus) {
            start.linkTo(latestStatusLabel.end, margin = 10.dp)
            top.linkTo(casteLabel.top)
            bottom.linkTo(casteLabel.bottom)
//            end.linkTo(parent.end, margin = 10.dp)
//            width = Dimension.fillToConstraints
        }
        constrain(dadaNameLabel) {
            start.linkTo(houseNumberLabel.start)
            top.linkTo(latestStatusLabel.bottom, margin = 20.dp)
//            end.linkTo(centerGuideline)
//            width = Dimension.fillToConstraints
        }

        constrain(caste) {
            start.linkTo(casteLabel.end, margin = 10.dp)
            top.linkTo(dadaNameLabel.top)
            bottom.linkTo(dadaNameLabel.bottom)
//            end.linkTo(parent.end, margin = 10.dp)
//            width = Dimension.fillToConstraints
        }
        constrain(tolaLabel) {
            start.linkTo(houseNumberLabel.start)
            top.linkTo(dadaNameLabel.bottom, margin = 15.dp)
//            end.linkTo(centerGuideline)
//            width = Dimension.fillToConstraints
        }

        constrain(tola) {
            start.linkTo(tolaLabel.end, margin = 10.dp)
            top.linkTo(tolaLabel.top)
            bottom.linkTo(tolaLabel.bottom)
//            end.linkTo(parent.end, margin = 10.dp)
//            width = Dimension.fillToConstraints
        }

        constrain(bottomPadding) {
            start.linkTo(parent.start)
            top.linkTo(latestStatus.bottom)
        }
    }
}