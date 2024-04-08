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
import com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component.OptionItemEntityState

@Composable
fun TypeMultiSelectedDropDownComponent(
    title: String? = BLANK_STRING,
    hintText: String = "Select",
    sources: List<String>?,
    isContent: Boolean = false,
    showQuestionState: OptionItemEntityState? = OptionItemEntityState.getEmptyStateObject(),
    selectOptionText: String = BLANK_STRING,
    onInfoButtonClicked: () -> Unit,
    onAnswerSelection: (selectValue: String) -> Unit,
) {
    val defaultSourceList = sources ?: listOf("Yes", "No")
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var selectedItems by remember { mutableStateOf(emptyList<String>()) }

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
                onAnswerSelection(if (selectedItems.isNotEmpty()) selectedItems.joinToString(", ") else "Select Items")
            },
            onInfoButtonClicked = {
                onInfoButtonClicked()
            }
        )
    }
}