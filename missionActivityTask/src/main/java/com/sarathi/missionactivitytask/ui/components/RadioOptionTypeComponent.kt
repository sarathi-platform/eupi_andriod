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
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_0_dp
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.languageItemInActiveBorderBg
import com.nudge.core.ui.theme.lightGray2
import com.nudge.core.ui.theme.lightGrayColor
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel

@Composable
fun RadioOptionTypeComponent(
    optionItemEntityState: List<OptionsUiModel>,
    isTaskMarkedNotAvailable: MutableState<Boolean> = mutableStateOf(false),
    selectedValue: String = BLANK_STRING,
    isActivityCompleted: Boolean,
    onOptionSelected: (index: Int, optionValue: String, optionId: Int) -> Unit
) {
    val yesNoButtonViewHeight = remember {
        mutableStateOf(dimen_0_dp)
    }
    val localDensity = LocalDensity.current

    val selectedValueState =
        remember(selectedValue, optionItemEntityState, isTaskMarkedNotAvailable) {
            if (isTaskMarkedNotAvailable.value)
                mutableStateOf(BLANK_STRING)
            else
                mutableStateOf(selectedValue)
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimen_16_dp)

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
                        textColor = selectTextColor(
                            selectedValueState,
                            optionValueText,
                            isTaskMarkedNotAvailable
                        ),
                        backgroundColor = selectBackgroundColor(
                            selectedValueState,
                            optionValueText,
                            isTaskMarkedNotAvailable
                        ),
                        borderColor = getBorderColor(
                            selectedValueState = selectedValueState,
                            optionValueText = optionValueText,
                            isTaskMarkedNotAvailable = isTaskMarkedNotAvailable
                        ),
                        optionText = optionValueText.description.toString()
                    ) {
                        if (!isActivityCompleted) {
                            selectedValueState.value = optionValueText.description.toString()
                            isTaskMarkedNotAvailable.value = false
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
fun selectBackgroundColor(
    selectedValueState: MutableState<String>,
    optionValueText: OptionsUiModel,
    isTaskMarkedNotAvailable: MutableState<Boolean>
): Color {
    return if (isTaskMarkedNotAvailable.value)
        lightGrayColor
    else {
        if (selectedValueState.value == BLANK_STRING
            && optionValueText.selectedValue.toString() == BLANK_STRING
        ) {
            Color.White
        } else if (selectedValueState.value.equals(
                optionValueText.description.toString(), ignoreCase = true
            )
        ) {
            blueDark
        } else {
            Color.White
        }
    }
}

@Composable
fun getBorderColor(
    selectedValueState: MutableState<String>,
    optionValueText: OptionsUiModel,
    isTaskMarkedNotAvailable: MutableState<Boolean>
): Color {
    return if (isTaskMarkedNotAvailable.value)
        languageItemInActiveBorderBg
    else {
        if (selectedValueState.value == BLANK_STRING
            && optionValueText.selectedValue.toString() == BLANK_STRING
        ) {
            lightGray2
        } else if (selectedValueState.value.equals(
                optionValueText.description.toString(), ignoreCase = true
            )
        ) {
            blueDark
        } else {
            languageItemInActiveBorderBg
        }
    }
}

@Composable
fun selectTextColor(
    selectedValueState: MutableState<String>,
    optionValueText: OptionsUiModel,
    isTaskMarkedNotAvailable: MutableState<Boolean>
): Color {

    return if (isTaskMarkedNotAvailable.value)
        lightGrayColor
    else {
        if (selectedValueState.value == BLANK_STRING
            && optionValueText.selectedValue.toString() == BLANK_STRING
        ) {
            textColorDark
        } else if (selectedValueState.value.equals(
                optionValueText.description.toString(), ignoreCase = true
            )
        ) {
            white
        } else {
            textColorDark
        }
    }
}

@Composable
fun OptionCard(
    modifier: Modifier,
    textColor: Color,
    optionText: String,
    backgroundColor: Color,
    borderColor: Color,
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
            .border(
                dimen_1_dp, color = borderColor, RoundedCornerShape(dimen_6_dp)
            )
            .then(modifier)

    ) {
        Text(
            text = optionText, color = textColor
        )
    }
}


