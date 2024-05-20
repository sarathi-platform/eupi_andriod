package com.nrlm.baselinesurvey.ui.surveyee_screen.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.ALL_TAB
import com.nrlm.baselinesurvey.HUSBAND_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.greenOnline
import com.nrlm.baselinesurvey.ui.theme.smallerTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorBlueLight
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.yellowBg
import com.nrlm.baselinesurvey.utils.states.SurveyState
import com.nrlm.baselinesurvey.utils.states.SurveyeeCardState

@Composable
fun SurveyeeCardWithTolaFilterComponent(
    modifier: Modifier = Modifier,
    tolaName: String,
    surveyeeStateList: List<SurveyeeCardState>,
    showCheckBox: Boolean,
    fromScreen: String,
    primaryButtonText: String = stringResource(R.string.start_baseline),
    checkBoxChecked: (surveyeeEntity: SurveyeeEntity, isChecked: Boolean) -> Unit,
    moveDidiToThisWeek: (surveyeeCardState: SurveyeeCardState, moveToThisWeek: Boolean) -> Unit,
    buttonClicked: (buttonName: ButtonName, surveyeeId: Int) -> Unit
) {
    Column() {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 16.dp, bottom = 10.dp, top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_icn),
                contentDescription = "home image",
                modifier = Modifier
                    .size(18.dp),
                colorFilter = ColorFilter.tint(textColorBlueLight)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = tolaName.capitalize(),
                style = defaultTextStyle,
                color = textColorDark,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(end = 10.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = yellowBg,
                        shape = CircleShape
                    )
                    .background(
                        yellowBg,
                        shape = CircleShape
                    )
                    .padding(6.dp)
                    .size(24.dp)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${surveyeeStateList.size}",
                    color = greenOnline,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .absolutePadding(bottom = 3.dp),
                    style = smallerTextStyle
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(dimen_10_dp)) {
            surveyeeStateList.forEachIndexed { index, surveyeeCardState ->
                SurveyeeCardComponent(
                    surveyeeState = surveyeeCardState,
                    showCheckBox = showCheckBox,
                    fromScreen = fromScreen,
                    primaryButtonText = primaryButtonText,
                    checkBoxChecked = { surveyeeEntity, isChecked ->
                        checkBoxChecked(surveyeeEntity, isChecked)
                    },
                    buttonClicked = { buttonName, surveyeeId ->
                        buttonClicked(buttonName, surveyeeId)
                    },
                    moveDidiToThisWeek = { surveyeeEntity, isChecked ->
                        moveDidiToThisWeek(surveyeeEntity, isChecked)
                    }
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SurveyeeCardWithTolaFilterComponentPreview() {
    val didi1 = SurveyeeEntity(
        id = 0,
        didiId = 1,
        didiName = "Shanti Devi",
        dadaName = "Manoj Parhaiya",
        houseNo = "A2",
        casteId = 1,
        cohortId = 1,
        cohortName = "Sundar Pahar",
        relationship = HUSBAND_STRING,
        villageId = 1,
        villageName = "Sundar Pahar",
        ableBodied = "No",
        userId = ""
    )
    val didi2 = SurveyeeEntity(
        id = 0,
        didiId = 1,
        didiName = "Shanti Devi",
        dadaName = "Manoj Parhaiya",
        houseNo = "A2",
        casteId = 1,
        cohortId = 1,
        cohortName = "Sundar Pahar",
        relationship = HUSBAND_STRING,
        villageId = 1,
        villageName = "Sundar Pahar",
        ableBodied = "No",
        userId = ""
    )
    val surveyeeCardState1 = SurveyeeCardState(
        surveyeeDetails = didi1,
        subtitle = didi1.didiName,
        address = didi1.houseNo + ",  " + didi1.cohortName,
        activityName = "Conduct BaseLine Survey",
        surveyState = SurveyState.NOT_STARTED
    )
    val surveyeeCardState2 = SurveyeeCardState(
        surveyeeDetails = didi2,
        subtitle = didi2.didiName,
        address = didi2.houseNo + ",  " + didi2.cohortName,
        activityName = "Conduct Hamlet Survey",
        surveyState = SurveyState.NOT_STARTED
    )
    val surveyeeStateList1 = listOf<SurveyeeCardState>(surveyeeCardState1, surveyeeCardState1, surveyeeCardState1)
    val surveyeeStateList2 = listOf<SurveyeeCardState>(surveyeeCardState2, surveyeeCardState2)

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(dimen_8_dp)) {
        SurveyeeCardWithTolaFilterComponent(
            surveyeeStateList = surveyeeStateList1,
            tolaName = "Sundar Pahar",
            showCheckBox = false,
            fromScreen = ALL_TAB,
            checkBoxChecked = { surveyeeEntity, isChecked ->

            },
            moveDidiToThisWeek = { surveyeeCardState, moveToThisWeek ->

            },
            buttonClicked = { buttonName, surveyeeId ->

            }
        )
        SurveyeeCardWithTolaFilterComponent(
            surveyeeStateList = surveyeeStateList2,
            tolaName = "Sundar Pahar 2",
            showCheckBox = false,
            fromScreen = ALL_TAB,
            checkBoxChecked = { surveyeeEntity, isChecked ->

            },
            moveDidiToThisWeek = { surveyeeCardState, moveToThisWeek ->

            },
            buttonClicked = { buttonName, surveyeeId ->

            }
        )
    }
}