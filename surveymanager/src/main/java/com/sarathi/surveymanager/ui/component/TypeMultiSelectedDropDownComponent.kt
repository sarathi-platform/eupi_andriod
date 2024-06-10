package com.sarathi.surveymanager.ui.component


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.toSize
import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.constants.DELIMITER_MULTISELECT_OPTIONS

@Composable
fun TypeMultiSelectedDropDownComponent(
    hintText: String = stringResource(R.string.select),
    sources: List<ValuesDto>?,
    selectOptionText: String = BLANK_STRING,
    onAnswerSelection: (selectValue: String) -> Unit,
) {

    val defaultSourceList = sources ?: listOf(ValuesDto(id = 1, "Yes"), ValuesDto(id = 2, "No"))
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var selectedItems by remember {
        if (selectOptionText.equals(BLANK_STRING, true))
            mutableStateOf(emptyList())
        else
            mutableStateOf(selectOptionText.split(DELIMITER_MULTISELECT_OPTIONS))
    }

    MultiSelectSelectDropDown(
        hint = hintText,
        items = defaultSourceList,
        modifier = Modifier.fillMaxWidth(),
        mTextFieldSize = textFieldSize,
        expanded = expanded,
        selectedItems = selectedItems,
        onExpandedChange = {
            expanded = !it
        },
        onDismissRequest = {
            expanded = false
        },
        onGlobalPositioned = { coordinates ->
            textFieldSize = coordinates.size.toSize()
        },
        onItemSelected = { selectedItem ->
            selectedItems = if (selectedItems.contains(selectedItem)) {
                selectedItems - selectedItem
            } else {
                selectedItems + selectedItem
            }
            onAnswerSelection(selectedItems.joinToString(", "))
        }
    )
}