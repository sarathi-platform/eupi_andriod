package com.nrlm.baselinesurvey.ui.surveyee_screen.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.nrlm.baselinesurvey.ALL_TAB
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.HUSBAND_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.ui.common_components.CircularImageViewComponent
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.borderGreyLight
import com.nrlm.baselinesurvey.ui.theme.brownDark
import com.nrlm.baselinesurvey.ui.theme.buttonBgColor
import com.nrlm.baselinesurvey.ui.theme.completeTickColor
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_3_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_4_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_6_dp
import com.nrlm.baselinesurvey.ui.theme.greenDark
import com.nrlm.baselinesurvey.ui.theme.mediumTextStyle
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleMediumWeight
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.textColorDark80
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.states.SurveyState
import com.nrlm.baselinesurvey.utils.states.SurveyeeCardState
import com.nrlm.baselinesurvey.utils.toCamelCase

@Composable
fun SurveyeeCardComponent(
    modifier: Modifier = Modifier,
    surveyeeState: SurveyeeCardState,
    showCheckBox: Boolean,
    fromScreen: String,
    primaryButtonText: String = "Start Baseline",
    checkBoxChecked: (surveyeeEntity: SurveyeeEntity, isChecked: Boolean) -> Unit,
    buttonClicked: (buttonName: ButtonName, surveyeeId: Int) -> Unit,
    moveDidiToThisWeek: (surveyeeCardState: SurveyeeCardState, moveToThisWeek: Boolean) -> Unit
) {

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = defaultCardElevation
        ),
        shape = RoundedCornerShape(roundedCornerRadiusDefault),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                //Handle Click if any
            }
            .then(modifier)
    ) {

        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopEnd)
                    .zIndex(1f),
                contentAlignment = Alignment.TopEnd,
            ) {
                if (fromScreen == ALL_TAB) {
                    if (showCheckBox) {
                        Surface(
                            elevation = dimen_4_dp,
                            shape = RoundedCornerShape(
                                topEnd = dimen_6_dp,
                                bottomStart = dimen_6_dp
                            )
                        ) {

//                            IconButton(
//                                onClick = {
//                                    moveDidiToThisWeek(surveyeeState, true)
//                                },
//                                modifier = Modifier
//                                    .background(Color.White)
//                            ) {
//                                Image(
//                                    painter = painterResource(id = R.drawable.convert_check_box),
//                                    contentDescription = ""
//                                )
//                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(
                                topEnd = dimen_6_dp,
                                bottomStart = dimen_6_dp
                            )
                        ) {
                            Checkbox(modifier = Modifier,
                                checked = surveyeeState.isChecked.value,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = greenDark,
                                    uncheckedColor = textColorDark, checkmarkColor = buttonBgColor
                                ),
                                onCheckedChange = {
                                    surveyeeState.isChecked.value = it
                                    checkBoxChecked(surveyeeState.surveyeeDetails, it)
                                })
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(white)
                    .padding(dimen_10_dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimen_4_dp),
                    horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!surveyeeState.activityName.equals("Conduct Hamlet Survey"))
                        CircularImageViewComponent(modifier = Modifier, surveyeeState.imagePath)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text =  if (!surveyeeState.activityName.equals("Conduct Hamlet Survey")) surveyeeState.surveyeeDetails.didiName else surveyeeState.surveyeeDetails.cohortName,
                            style = mediumTextStyle,
                            color = brownDark
                        )
                        if (surveyeeState.subtitle != BLANK_STRING && !surveyeeState.activityName.equals("Conduct Hamlet Survey")) {
                            Text(
                                text = surveyeeState.subtitle,
                                style = smallTextStyleMediumWeight,
                                color = textColorDark80
                            )
                        }
                        if (surveyeeState.address != BLANK_STRING) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.home_icn),
                                    contentDescription = "home icon",
                                    tint = Color.Black,
                                    modifier = Modifier.height(dimen_18_dp)
                                )
                                Spacer(modifier = Modifier.width(dimen_3_dp))
                                Text(

                                    text = if (!surveyeeState.activityName.equals("Conduct Hamlet Survey")) surveyeeState.address.toLowerCase(Locale.current).toCamelCase()
                                    else surveyeeState.surveyeeDetails.villageName.toLowerCase().toCamelCase(),
                                    style = smallTextStyleMediumWeight,
                                    color = textColorDark
                                )
                            }
                        }
                    }
                }

                if (surveyeeState.surveyState != SurveyState.COMPLETED) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimen_18_dp),
                        horizontalArrangement = Arrangement.spacedBy(dimen_10_dp)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
//                        Button(
//                            onClick = {
//                                buttonClicked(
//                                    ButtonName.NEGATIVE_BUTTON,
//                                    surveyeeState.surveyeeDetails.didiId ?: 0
//                                )
//                            },
//                            enabled = true,
//                            shape = RoundedCornerShape(roundedCornerRadiusDefault),
//                            border = BorderStroke(dimen_1_dp, borderGreyLight),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = languageItemActiveBg,
//                                contentColor = blueDark
//                            ),
//                            modifier = Modifier.weight(1f)
//                        ) {
//                            Text(text = "Not Available", style = smallTextStyleNormalWeight)
//                        }
                        if (surveyeeState.surveyState == SurveyState.INPROGRESS) {
                            Button(
                                onClick = {
                                    buttonClicked(
                                        ButtonName.CONTINUE_BUTTON,
                                        surveyeeState.surveyeeDetails.didiId ?: 0
                                    )
                                },
                                enabled = true,
                                shape = RoundedCornerShape(roundedCornerRadiusDefault),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = blueDark,
                                    contentColor = white
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Continue", style = smallTextStyleMediumWeight)
                            }
                        } else if (surveyeeState.surveyState == SurveyState.NOT_STARTED) {
                            Button(
                                onClick = {
                                    buttonClicked(
                                        ButtonName.START_BUTTON,
                                        surveyeeState.surveyeeDetails.didiId ?: 0
                                    )
                                },
                                enabled = true,
                                shape = RoundedCornerShape(roundedCornerRadiusDefault),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = blueDark,
                                    contentColor = white
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = primaryButtonText, style = smallTextStyleMediumWeight)
                            }
                        }
                    }
                }
                else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimen_18_dp),
                        horizontalArrangement = Arrangement.spacedBy(dimen_10_dp)
                    ) {
//                        Button(
//                            onClick = {
//                                buttonClicked(
//                                    ButtonName.EXPORT_BUTTON,
//                                    surveyeeState.surveyeeDetails.didiId ?: 0
//                                )
//                            },
//                            enabled = true,
//                            shape = RoundedCornerShape(roundedCornerRadiusDefault),
//                            border = BorderStroke(dimen_1_dp, blueDark),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = white,
//                                contentColor = blueDark
//                            ),
//                            modifier = Modifier.weight(1f)
//                        ) {
//                            Text(text = "Export", style = smallTextStyleNormalWeight)
//                        }
                        Button(
                            onClick = {
                                buttonClicked(
                                    ButtonName.SHOW_BUTTON,
                                    surveyeeState.surveyeeDetails.didiId ?: 0
                                )
                            },
                            enabled = true,
                            shape = RoundedCornerShape(roundedCornerRadiusDefault),
                            border = BorderStroke(dimen_1_dp, borderGreyLight),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = white
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_completed_tick),
                                contentDescription = "",
                                tint = completeTickColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SurveyeeCardPreview() {
    val didi = SurveyeeEntity(
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
        userId = 525,
        surveyStatus = SurveyState.COMPLETED.ordinal
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        SurveyeeCardComponent(
            surveyeeState = SurveyeeCardState(
                surveyeeDetails = didi,
                subtitle = didi.didiName,
                address = didi.houseNo + ",  " + didi.cohortName,
                activityName =  "Conduct Hamlet Survey",
                surveyState = SurveyState.NOT_STARTED
            ),
            showCheckBox = true,
            fromScreen = ALL_TAB,
            checkBoxChecked = { surveyeeEntity, isChecked ->

            },
            moveDidiToThisWeek = { surveyeeCardState: SurveyeeCardState, moveToThisWeek: Boolean ->
            },
            primaryButtonText = "",
            buttonClicked = { buttonName, surveyeeId ->

            }
        )
    }

}

sealed class ButtonName {
    object START_BUTTON : ButtonName()
    object CONTINUE_BUTTON : ButtonName()
    object NEGATIVE_BUTTON : ButtonName()
    object SHOW_BUTTON : ButtonName()
    object EXPORT_BUTTON: ButtonName()
}