package com.sarathi.surveymanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.commonUi.IncrementDecrementCounter
import com.nudge.core.ui.commonUi.MAXIMUM_RANGE
import com.nudge.core.ui.commonUi.QuestionComponent
import com.nudge.core.ui.theme.defaultCardElevation
import com.nudge.core.ui.theme.dimen_0_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.white
import com.nudge.core.value
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel

@Composable
fun IncrementDecrementCounterList(
    title: String = BLANK_STRING,
    optionList: List<OptionsUiModel>?,
    isEditAllowed: Boolean = true,
    maxValue: Int = MAXIMUM_RANGE,
    editNotAllowedMsg: String = BLANK_STRING,
    isMandatory: Boolean = false,
    showCardView: Boolean = false,
    onAnswerSelection: (optionId: Int?, selectValue: String) -> Unit,
) {

    BasicCardView(
        cardElevation = CardDefaults.cardElevation(
            defaultElevation = if (showCardView) defaultCardElevation else dimen_0_dp
        ),
        cardShape = RoundedCornerShape(roundedCornerRadiusDefault),
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .padding(horizontal = if (showCardView) dimen_16_dp else dimen_0_dp)
        ) {
            if (title?.isNotBlank() == true) {
                QuestionComponent(title = title, isRequiredField = isMandatory)
            }

            optionList?.forEach { it ->
                IncrementDecrementCounter(
                    label = it.description,
                    isEditAllowed = isEditAllowed,
                    currentCount = it.selectedValue.value("0"),
                    maxValue = maxValue,
                    editNotAllowedMsg = editNotAllowedMsg,
                    onAnswerSelection = { selectValue ->
                        if (selectValue != BLANK_STRING) {
                            onAnswerSelection(it.optionId, selectValue)
                        }
                    }
                )
            }

        }
    }

}