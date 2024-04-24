package com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.toSize
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.common_components.DropDownWithTitleComponent
import com.nrlm.baselinesurvey.ui.common_components.VerticalAnimatedVisibilityComponent
import com.nrlm.baselinesurvey.utils.showCustomToast

@Composable
fun TypeDropDownComponent(
    title: String?,
    hintText: String = "Select",
    sources: List<String>?,
    isEditAllowed: Boolean = true,
    isContent: Boolean = false,
    showQuestionState: OptionItemEntityState? = OptionItemEntityState.getEmptyStateObject(),
    selectOptionText: String = BLANK_STRING,
    onInfoButtonClicked: () -> Unit,
    onAnswerSelection: (selectValue: String) -> Unit
    ) {

    val defaultSourceList = if (sources == null) listOf("Yes", "No") else sources
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(if (selectOptionText == BLANK_STRING) hintText else selectOptionText) }
    if(!defaultSourceList.contains(selectedOptionText)){
        selectedOptionText= BLANK_STRING
    }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val context = LocalContext.current

    VerticalAnimatedVisibilityComponent(visible = showQuestionState?.showQuestion ?: true) {
        DropDownWithTitleComponent(
            title = title ?: "",
            items = defaultSourceList ?: listOf(),
            modifier = Modifier.fillMaxWidth(),
            mTextFieldSize = textFieldSize,
            expanded = expanded,
            selectedItem = selectedOptionText,
            isContent = isContent,
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
                    selectedOptionText = defaultSourceList?.get(defaultSourceList.indexOf(it)) ?: ""
                    onAnswerSelection(selectedOptionText)
                    expanded = false
                } else {
                    showCustomToast(
                        context,
                        context.getString(R.string.edit_disable_message)
                    )
                }
            },
            onInfoButtonClicked = {
                onInfoButtonClicked()
            }
        )
    }
}