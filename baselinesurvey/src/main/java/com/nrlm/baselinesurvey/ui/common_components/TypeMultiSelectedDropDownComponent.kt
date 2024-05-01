package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.toSize
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.model.datamodel.ValuesDto
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState

@Composable
fun TypeMultiSelectedDropDownComponent(
    title: String? = BLANK_STRING,
    hintText: String = "Select",
    sources: List<ValuesDto>?,
    isContent: Boolean = false,
    showQuestionState: OptionItemEntityState? = OptionItemEntityState.getEmptyStateObject(),
    selectOptionText: String = BLANK_STRING,
    onInfoButtonClicked: () -> Unit,
    onAnswerSelection: (selectValue: String) -> Unit,
) {
    //TODO handle everything using id

    val defaultSourceList = sources ?: listOf(ValuesDto(id = 1, "Yes"), ValuesDto(id = 2, "No"))
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var selectedItems by remember {
        if (selectOptionText.equals(BLANK_STRING, true))
            mutableStateOf(emptyList<String>())
        else
            mutableStateOf(selectOptionText.split(", "))
    }

    VerticalAnimatedVisibilityComponent(visible = showQuestionState?.showQuestion ?: true) {
        MultiSelectDropdown(
            title = title ?: "",
            items = defaultSourceList,
            modifier = Modifier.fillMaxWidth(),
            mTextFieldSize = textFieldSize,
            expanded = expanded,
            isContent = isContent,
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
            },
            onInfoButtonClicked = {
                onInfoButtonClicked()
            }
        )
    }
}