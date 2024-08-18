package com.sarathi.missionactivitytask.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.GreyLight
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_0_dp
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.lightGray2
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel

@Composable
fun RadioOptionTypeComponent(
    optionItemEntityState: List<OptionsUiModel>,
    isTaskMarkedNotAvailable: MutableState<Boolean> = mutableStateOf(false),
    selectedValue: String = BLANK_STRING,
    onOptionSelected: (index: Int, optionValue: String, optionId: Int) -> Unit
) {
    val yesNoButtonViewHeight = remember {
        mutableStateOf(dimen_0_dp)
    }
    val localDensity = LocalDensity.current

    val selectedValueState = remember(selectedValue, optionItemEntityState) {
        mutableStateOf(selectedValue)
    }

    Column(
        modifier = Modifier.fillMaxWidth()

    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimen_10_dp)
                .clip(RoundedCornerShape(dimen_6_dp))
                .background(
                    white, shape = RoundedCornerShape(dimen_6_dp)
                )
                .padding(0.dp)
        ) {
            Row(
                Modifier.onGloballyPositioned { coordinates ->
                    yesNoButtonViewHeight.value =
                        with(localDensity) { coordinates.size.height.toDp() }

                },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(dimen_5_dp))
                optionItemEntityState.forEachIndexed { index, optionValueText ->
                    OptionCard(
                        modifier = Modifier.weight(1f),
                        textColor = if (selectedValueState.value.equals(
                                optionValueText.description, ignoreCase = true
                            )
                        ) white else textColorDark,
                        backgroundColor = selectBackgroundColor(
                            selectedValueState,
                            optionValueText,
                            isTaskMarkedNotAvailable
                        ),
                        optionText = optionValueText.description.toString()
                    ) {
                        if (!isTaskMarkedNotAvailable.value) {
                            selectedValueState.value = optionValueText.description.toString()
                            onOptionSelected(
                                index,
                                optionValueText.description.toString(),
                                optionValueText.optionId ?: -1
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(dimen_5_dp))
                }
            }
        }
    }


}

@Composable
private fun selectBackgroundColor(
    selectedValueState: MutableState<String>,
    optionValueText: OptionsUiModel,
    isTaskMarkedNotAvailable: MutableState<Boolean>
) = if (selectedValueState.value == BLANK_STRING
    && optionValueText.selectedValue.toString() == BLANK_STRING
) {
    if (isTaskMarkedNotAvailable.value) GreyLight else Color.White
} else if (selectedValueState.value.equals(
        optionValueText.description.toString(), ignoreCase = true
    )
) blueDark else {
    if (isTaskMarkedNotAvailable.value) GreyLight else Color.White
}

@Composable
fun OptionCard(
    modifier: Modifier,
    textColor: Color,
    optionText: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    TextButton(
        onClick = {
            onClick()
        }, modifier = Modifier
            .background(
                backgroundColor, RoundedCornerShape(
                    dimen_6_dp
                )
            )
            .border(dimen_1_dp, color = lightGray2, RoundedCornerShape(dimen_6_dp))
            .then(modifier)

    ) {
        Text(
            text = optionText, color = textColor
        )
    }
}


