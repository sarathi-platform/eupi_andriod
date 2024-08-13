package com.sarathi.missionactivitytask.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel

@Composable
fun MultiSelectToggleComponent(
    optionItemEntityState: List<OptionsUiModel>,
    isMandatory: Boolean = false,
    isContent: Boolean = false,
    isTaskMarkedNotAvailable: MutableState<Boolean> = mutableStateOf(false),
    selectedValue: String = BLANK_STRING,
    onOptionSelected: (index: Int, optionValue: String, optionId: Int) -> Unit
) {
    val yesNoButtonViewHeight = remember {
        mutableStateOf(0.dp)
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
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    white, shape = RoundedCornerShape(6.dp)
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
                Spacer(modifier = Modifier.width(5.dp))
                optionItemEntityState?.forEachIndexed { index, optionValueText ->
                    Log.d("TAG", "RadioOptionTypeComponent: ${isTaskMarkedNotAvailable.value}")
                    OptionCard(
                        modifier = Modifier.weight(1f),
                        textColor = if (selectedValueState.value.equals(
                                optionValueText.description, ignoreCase = true
                            )
                        ) white else textColorDark,
                        backgroundColor = if (selectedValueState.value == BLANK_STRING && optionValueText.selectedValue.toString() == BLANK_STRING) {
                            if (isTaskMarkedNotAvailable.value) GreyLight else Color.White
                        } else if (selectedValueState.value.equals(
                                optionValueText.description.toString(), ignoreCase = true
                            )
                        ) blueDark else {
                            if (isTaskMarkedNotAvailable.value) GreyLight else Color.White
                        },
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
                    Spacer(modifier = Modifier.width(5.dp))
                }
            }
        }
    }


}


