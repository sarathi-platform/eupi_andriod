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
import com.nrlm.baselinesurvey.model.datamodel.ValuesDto
import com.nrlm.baselinesurvey.model.datamodel.contains
import com.nrlm.baselinesurvey.ui.common_components.DropDownWithTitleComponent
import com.nrlm.baselinesurvey.ui.common_components.VerticalAnimatedVisibilityComponent
import com.nrlm.baselinesurvey.utils.showCustomToast

@Composable
fun TypeDropDownComponent(
    title: String?,
    hintText: String = "Select",
    sources: List<ValuesDto>?,
    isEditAllowed: Boolean = true,
    isContent: Boolean = false,
    showQuestionState: OptionItemEntityState? = OptionItemEntityState.getEmptyStateObject(),
    selectOptionText: Int = 0,
    isDidiReassigned: Boolean = false,
    onInfoButtonClicked: () -> Unit,
    onAnswerSelection: (selectValue: Int) -> Unit
) {
//TODO handle everything using id

    val defaultSourceList =
        if (sources == null) listOf(ValuesDto(id = 1, "Yes"), ValuesDto(id = 2, "No")) else sources
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember {
        mutableStateOf(
            if (selectOptionText == 0) hintText else defaultSourceList.find { it.id == selectOptionText }?.value
                ?: hintText
        )
    }
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
                    selectedOptionText =
                        defaultSourceList?.get(defaultSourceList.indexOf(it))?.value ?: ""
                    onAnswerSelection(it.id)
                    expanded = false
                } else {
                    showCustomToast(
                        context,
                        context.getString(
                            if (isDidiReassigned)
                                R.string.beneficiary_is_reassigned_to_another_upcm
                            else R.string.edit_disable_message
                        )
                    )
                }
            },
            onInfoButtonClicked = {
                onInfoButtonClicked()
            }
        )
    }
}