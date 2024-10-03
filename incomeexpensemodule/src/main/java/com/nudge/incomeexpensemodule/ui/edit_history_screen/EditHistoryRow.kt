package com.nudge.incomeexpensemodule.ui.edit_history_screen

import android.text.TextUtils
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.incomeexpensemodule.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.BLANK_STRING
import com.nudge.core.DD_MMM_YYYY_FORMAT
import com.nudge.core.DD_mmm_hh_mm_a_FORMAT
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
import com.sarathi.dataloadingmangement.enums.LivelihoodEventTypeDataCaptureMapping.Companion.getLivelihoodEventFromName
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventScreenData


@Composable
fun EditHistoryRow(
    currentHistoryData: SubjectLivelihoodEventMappingEntity,
    nextHistoryData: SubjectLivelihoodEventMappingEntity?,
    isDeleted: Boolean = false,
    isRecentData: Boolean = false
) {
    Row(
        modifier = Modifier
            .background(Color.Transparent)

    ) {
        val currentSavedEvent = getSavedEventData(currentHistoryData.surveyResponse)
        val nextSavedEvent = getSavedEventData(nextHistoryData?.surveyResponse)
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
                            text = stringResource(R.string.delete),
                            style = smallTextStyle.copy(redIconColor)
                        )
                    }
                }
                Column(modifier = Modifier.padding(dimen_10_dp)) {
                    TextDataRowView(
                        data1 = stringResource(R.string.event),
                        data2textColor = dataChangeTextColor(
                            data1 = getEventValue(savedEvent = currentSavedEvent),
                            data2 = getEventValue(savedEvent = nextSavedEvent),
                        ),
                        data2 = " ${currentSavedEvent?.eventValue ?: BLANK_STRING}",
                        data3 = currentHistoryData.modifiedDate.getDate(DD_mmm_hh_mm_a_FORMAT)
                    )
                    Divider()
                    Spacer(modifier = Modifier.height(4.dp))
                    TextDataRowView(
                        data1 = stringResource(R.string.asset_type),
                        data2 = if (!TextUtils.isEmpty(currentSavedEvent?.assetTypeValue)) " ${currentSavedEvent?.assetTypeValue}" else "NA",
                        data2textColor = dataChangeTextColor(
                            data1 = getAssetTypeValue(savedEvent = currentSavedEvent),
                            data2 = getAssetTypeValue(savedEvent = nextSavedEvent),
                        ),
                        data3 = if (getLivelihoodEventFromName(currentHistoryData.livelihoodEventType).moneyJournalEntryFlowType != null) formatToIndianRupee(
                            "${currentSavedEvent?.amount}"
                        ) else BLANK_STRING,
                        data3TextColor = dataChangeTextColor(
                            data1 = getEventAmount(currentSavedEvent),
                            data2 = getEventAmount(nextSavedEvent),
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextDataRowView(
                        data1 = stringResource(R.string.increse_in_number),
                        data2textColor = dataChangeTextColor(
                            data1 = getAssetCount(currentSavedEvent),
                            data2 = getAssetCount(nextSavedEvent),
                        ),
                        data2 = " ${currentSavedEvent?.assetCount}"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextDataRowView(
                        data1 = stringResource(R.string.event_date),
                        data2 = " ${currentHistoryData.date.getDate(DD_MMM_YYYY_FORMAT)}",
                        data2textColor = dataChangeTextColor(
                            data1 = getEventDate(currentHistoryData),
                            data2 = getEventDate(nextHistoryData),
                        ),
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


@Composable
private fun TextDataRowView(
    data1: String? = null,
    data1TextColor: Color = blueDark,
    data2: String? = null,
    data2textColor: Color = blueDark,
    data3: String? = null,
    data3TextColor: Color = blueDark,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        data1?.let {
            Text(
                text = it,
                style = defaultTextStyle.copy(data1TextColor)
            )
        }
        data2?.let {
            Text(
                text = it,
                style = defaultTextStyle.copy(data2textColor)
            )
        }
        Spacer(modifier = Modifier.weight(1.0f))
        if (!TextUtils.isEmpty(data3)) {
            data3?.let {
                Text(
                    text = it,
                    style = defaultTextStyle.copy(data3TextColor)
                )
            }
        }

    }
}

private fun getSavedEventData(savedEvent: String?): LivelihoodEventScreenData? {
    savedEvent?.let {
        val type = object : TypeToken<LivelihoodEventScreenData?>() {}.type
        return Gson().fromJson<LivelihoodEventScreenData>(savedEvent, type)
    }
    return null
}

private fun dataChangeTextColor(data1: String?, data2: String?): Color {
    val isData1Invalid = data1.isNullOrEmpty() || data1 == BLANK_STRING || data1 == "0"
    val isData2Invalid = data2.isNullOrEmpty() || data2 == BLANK_STRING || data2 == "0"

    return when {
        isData1Invalid || isData2Invalid -> blueDark
        data1 != data2 -> darkBlueColor
        else -> blueDark
    }
}

private fun getEventValue(savedEvent: LivelihoodEventScreenData?): String =
    savedEvent?.assetTypeValue?.takeIf { it.isNotEmpty() && it.isNotBlank() } ?: BLANK_STRING

private fun getAssetTypeValue(savedEvent: LivelihoodEventScreenData?): String =
    savedEvent?.assetTypeValue?.takeIf { it.isNotEmpty() && it.isNotBlank() } ?: BLANK_STRING

private fun getEventAmount(savedEvent: LivelihoodEventScreenData?): String =
    savedEvent?.amount?.toString()?.takeIf { it.isNotEmpty() && it.isNotBlank() } ?: "0"

private fun getAssetCount(savedEvent: LivelihoodEventScreenData?): String =
    savedEvent?.assetCount?.toString()?.takeIf { it.isNotEmpty() && it.isNotBlank() } ?: "0"

private fun getEventDate(eventData: SubjectLivelihoodEventMappingEntity?): String =
    eventData?.date?.toString()?.takeIf { it.isNotEmpty() && it.isNotBlank() } ?: "0"

