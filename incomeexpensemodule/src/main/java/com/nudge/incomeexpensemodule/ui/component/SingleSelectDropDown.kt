package com.nudge.incomeexpensemodule.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.toSize
import com.example.incomeexpensemodule.R
import com.nudge.core.model.uiModel.ValuesDto
import com.nudge.core.showCustomToast
import com.nudge.core.ui.commonUi.DropDownComponent
import com.nudge.core.ui.theme.dimen_60_dp


@Composable
fun SingleSelectDropDown(
    modifier: Modifier = Modifier
        .fillMaxWidth(),
    height: Dp = dimen_60_dp,
    hintText: String = stringResource(R.string.select),
    sources: List<ValuesDto>?,
    isEditAllowed: Boolean = true,
    selectOptionText: Int = 0,
    onAnswerSelection: (selectValue: Int) -> Unit
) {
    val defaultSourceList =
        sources ?: listOf(ValuesDto(id = 1, "Yes"), ValuesDto(id = 2, "No"))
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember {
        mutableStateOf(
            if (selectOptionText == 0) hintText else defaultSourceList.find { it.id == selectOptionText }?.value
                ?: hintText
        )
    }
//    if (!defaultSourceList.contains(selectedOptionText)) {
//        selectedOptionText = BLANK_STRING
//    }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val context = LocalContext.current

    DropDownComponent(
        items = defaultSourceList,
        modifier = Modifier
            .then(modifier),
        height = height,
        mTextFieldSize = textFieldSize,
        expanded = expanded,
        selectedItem = selectedOptionText,
        onExpandedChange = {
            expanded = !it
        },
        onDismissRequest = {
            expanded = false
        },
        onGlobalPositioned = { coordinates ->
            textFieldSize = coordinates.size.toSize()
        },
        onItemSelected = {
            if (isEditAllowed) {
                selectedOptionText =
                    defaultSourceList[defaultSourceList.indexOf(it)].value
                onAnswerSelection(it.id)
                expanded = false
            } else {
                showCustomToast(
                    context,
                    context.getString(R.string.edit_disable_message)
                )
            }
        },

        )
}
