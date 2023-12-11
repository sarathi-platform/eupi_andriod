package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.HUSBAND_STRING
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.ui.theme.bannerBgGreen
import com.nrlm.baselinesurvey.ui.theme.bannerBorderGreen
import com.nrlm.baselinesurvey.ui.theme.bannerTextGreen
import com.nrlm.baselinesurvey.ui.theme.dimen_10_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_14_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_1_dp
import com.nrlm.baselinesurvey.ui.theme.roundedCornerRadiusDefault
import com.nrlm.baselinesurvey.ui.theme.smallTextStyle
import kotlinx.coroutines.delay

@Composable
fun MoveSurveyeeUpdateBannerComponent(
    modifier: Modifier = Modifier,
    showBanner: Boolean,
    surveyeeEntity: SurveyeeEntity?
) {
    val mShowBanner = remember {
        mutableStateOf(showBanner)
    }

    LaunchedEffect(key1 = mShowBanner.value) {
        if (mShowBanner.value) {
            delay(1000)
            mShowBanner.value = false
        }
    }
    
    Column(modifier = Modifier.then(modifier)) {
        AnimatedVisibility(visible = mShowBanner.value) {
            Box(
                modifier = Modifier
                    .border(
                        dimen_1_dp,
                        bannerBorderGreen,
                        RoundedCornerShape(roundedCornerRadiusDefault)
                    )
                    .fillMaxWidth()
                    .background(bannerBgGreen, RoundedCornerShape(roundedCornerRadiusDefault))
                    .padding(vertical = dimen_10_dp, horizontal = dimen_14_dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(text = "${surveyeeEntity?.didiName ?: BLANK_STRING} moved to this week", color = bannerTextGreen, style = smallTextStyle)
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MoveSurveyeeUpdateBannerComponentPreview() {
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
    val showBanner = remember {
        mutableStateOf(true)
    }
    Surface(modifier = Modifier.padding(horizontal = dimen_16_dp)) {
        MoveSurveyeeUpdateBannerComponent(showBanner = showBanner.value, surveyeeEntity = didi)
    }
}

@Composable
fun MoveSurveyeesUpdateBannerComponent(
    modifier: Modifier = Modifier,
    showBanner: MutableState<Boolean>,
    surveyeeIdList: Set<Int>
) {
    var mShowBanner = showBanner.value
    
    LaunchedEffect(key1 = showBanner.value) {
        if (showBanner.value) {
            delay(1000)
            mShowBanner = false
        }
    }
    
    Column(modifier = Modifier.then(modifier)) {
        AnimatedVisibility(visible = mShowBanner) {
            Box(
                modifier = Modifier
                    .border(
                        dimen_1_dp,
                        bannerBorderGreen,
                        RoundedCornerShape(roundedCornerRadiusDefault)
                    )
                    .fillMaxWidth()
                    .background(bannerBgGreen, RoundedCornerShape(roundedCornerRadiusDefault))
                    .padding(vertical = dimen_10_dp, horizontal = dimen_14_dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(text = "${surveyeeIdList.size} didis moved to this week", color = bannerTextGreen, style = smallTextStyle)
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun MoveSurveyeesUpdateBannerComponentPreview() {
    val showBanner = remember {
        mutableStateOf(true)
    }
    Surface(modifier = Modifier.padding(horizontal = dimen_16_dp)) {
        MoveSurveyeesUpdateBannerComponent(showBanner = showBanner, surveyeeIdList = setOf(1,2))
    }
}