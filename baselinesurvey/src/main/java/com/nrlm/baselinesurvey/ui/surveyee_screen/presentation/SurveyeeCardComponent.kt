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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.HUSBAND_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.ui.common_components.CircularImageViewComponent
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.borderGreyLight
import com.nrlm.baselinesurvey.ui.theme.brownDark
import com.nrlm.baselinesurvey.ui.theme.defaultCardElevation
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_4_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.languageItemActiveBg
import com.nrlm.baselinesurvey.ui.theme.mediumTextStyle
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleMediumWeight
import com.nrlm.baselinesurvey.ui.theme.smallTextStyleNormalWeight
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.textColorDark80
import com.nrlm.baselinesurvey.ui.theme.white
import com.nrlm.baselinesurvey.utils.SurveyState
import com.nrlm.baselinesurvey.utils.SurveyeeCardState

@Composable
fun SurveyeeCardComponent(
    modifier: Modifier = Modifier,
    surveyeeState: SurveyeeCardState,
    buttonClicked: (buttonName: ButtonName, surveyeeId: Int) -> Unit
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
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .padding(dimen_10_dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = dimen_4_dp),
                horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularImageViewComponent(modifier = Modifier, surveyeeState.imagePath)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = surveyeeState.surveyeeDetails.didiName, style = mediumTextStyle, color = brownDark)
                    if (surveyeeState.subtitle != BLANK_STRING) {
                        Text(
                            text = surveyeeState.subtitle,
                            style = smallTextStyleMediumWeight,
                            color = textColorDark80
                        )
                    }
                    if (surveyeeState.address != BLANK_STRING) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier) {
                            Icon(
                                painter = painterResource(id = R.drawable.home_icn),
                                contentDescription = "home icon",
                                tint = Color.Black,
                                modifier = Modifier.height(dimen_18_dp)
                            )
                            Spacer(modifier = Modifier.width(dimen_8_dp))
                            Text(
                                text = surveyeeState.address,
                                style = smallTextStyleMediumWeight,
                                color = textColorDark
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = dimen_18_dp),
                horizontalArrangement = Arrangement.spacedBy(dimen_10_dp)
            ) {
                Button(
                    onClick = { buttonClicked(ButtonName.NEGATIVE_BUTTON, surveyeeState.surveyeeDetails.didiId ?: 0) },
                    enabled = true,
                    shape = RoundedCornerShape(roundedCornerRadiusDefault),
                    border = BorderStroke(1.dp, borderGreyLight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = languageItemActiveBg,
                        contentColor = blueDark
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Not Available", style = smallTextStyleNormalWeight)
                }
                Button(
                    onClick = { buttonClicked(ButtonName.START_BUTTON, surveyeeState.surveyeeDetails.didiId ?: 0) },
                    enabled = true,
                    shape = RoundedCornerShape(roundedCornerRadiusDefault),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueDark,
                        contentColor = white
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Start Baseline", style = smallTextStyleMediumWeight)
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
        userId = 525
    )
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 10.dp)) {
        SurveyeeCardComponent(
            surveyeeState = SurveyeeCardState(
                surveyeeDetails = didi,
                subtitle = didi.didiName,
                address = didi.houseNo + ", " + didi.cohortName,
                surveyState = SurveyState.NOT_STARTED
            )
        ) { buttonName, surveyeeId ->

        }
    }

}

sealed class ButtonName {
    object START_BUTTON: ButtonName()
    object NEGATIVE_BUTTON: ButtonName()
    object SHOW_BUTTON: ButtonName()
}