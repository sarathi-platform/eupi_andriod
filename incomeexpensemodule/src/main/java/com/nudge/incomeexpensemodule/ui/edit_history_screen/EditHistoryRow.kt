package com.nudge.incomeexpensemodule.ui.edit_history_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.BLANK_STRING
import com.nudge.core.DD_MMM_YYYY_FORMAT
import com.nudge.core.DD_mmm_hh_MM_FORMAT
import com.nudge.core.formatToIndianRupee
import com.nudge.core.getDate
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.darkBlueColor
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.dividerColor
import com.nudge.core.ui.theme.grayColor
import com.nudge.core.ui.theme.incomeCardTopViewColor
import com.nudge.core.ui.theme.redIconColor
import com.nudge.core.ui.theme.smallTextStyle
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodEventMappingEntity
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData


@Composable
fun EditHistoryRow(
    event: SubjectLivelihoodEventMappingEntity,
    isDeleted: Boolean = false,
    isRecentData: Boolean = false
) {
    Row(
        modifier = Modifier
            .background(Color.Transparent)

    ) {
        val savedEvent = getData(event.surveyResponse)
        // Event details
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            BasicCardView(
                colors = CardDefaults.cardColors(
                    containerColor = white
                ),
                modifier = Modifier.padding(horizontal = dimen_10_dp)
            ) {
                if (isDeleted) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(incomeCardTopViewColor)
                    ) {
                        Spacer(modifier = Modifier.weight(1.0f))
                        Text(
                            modifier = Modifier.padding(horizontal = dimen_5_dp),
                            text = "Delete",
                            style = smallTextStyle.copy(redIconColor)
                        )
                    }
                }
                Column(modifier = Modifier.padding(dimen_10_dp)) {
                    TextRowView_1(
                        text1 = "Event:",
                        text2 = " ${savedEvent?.eventValue ?: BLANK_STRING}",
                        text3 = event.modifiedDate.getDate(DD_mmm_hh_MM_FORMAT)
                    )
                    Divider()
                    Spacer(modifier = Modifier.height(4.dp))
                    TextRowView_1(
                        text1 = "Asset Type:",
                        text2 = " ${event.livelihoodEventType}",
                        text3 = formatToIndianRupee("${savedEvent?.amount}"),
                        text3Color = if (!isRecentData) darkBlueColor else blueDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextRowView_1(
                        text1 = "Increase in Number:",
                        text2Color = if (isRecentData) darkBlueColor else blueDark,
                        text2 = " ${savedEvent?.assetCount}"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextRowView_1(
                        text1 = "Event Date:",
                        text2 = event.date.getDate(DD_MMM_YYYY_FORMAT)
                    )
                }
            }
        }

        // Dot and vertical line on the right
        Column(
            modifier = Modifier.padding(end = dimen_5_dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            SolidCircleWithBorder(
                circleColor = if (!isDeleted && isRecentData) darkBlueColor else Color.Transparent,
                borderColor = if (!isDeleted && isRecentData) darkBlueColor else grayColor,
                circleDiameter = 10,
                borderWidth = 2f
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (!isDeleted) {
                for (i in 1..17) {
                    androidx.compose.material.Divider(
                        color = dividerColor,
                        modifier = Modifier
                            .height(dimen_8_dp)
                            .width(dimen_1_dp)
                            .padding(vertical = dimen_2_dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun TextRowView_1(
    text1: String? = null,
    text1Color: Color = blueDark,
    text2: String? = null,
    text2Color: Color = blueDark,
    text3: String? = null,
    text3Color: Color = blueDark,

    ) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        text1?.let {
            Text(
                text = it,
                style = defaultTextStyle.copy(text1Color)
            )
        }
        text2?.let {
            Text(
                text = it,
                style = defaultTextStyle.copy(text2Color)
            )
        }
        Spacer(modifier = Modifier.weight(1.0f))
        text3?.let {
            Text(
                text = it,
                style = defaultTextStyle.copy(text3Color)
            )
        }
    }
}

private fun getData(savedEvent: String?): LivelihoodEventScreenData? {
    savedEvent?.let {
        val type = object : TypeToken<LivelihoodEventScreenData?>() {}.type
        return Gson().fromJson<LivelihoodEventScreenData>(savedEvent, type)
    }
    return null
}