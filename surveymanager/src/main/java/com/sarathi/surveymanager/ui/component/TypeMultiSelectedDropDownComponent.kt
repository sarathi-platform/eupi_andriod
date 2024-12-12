package com.sarathi.surveymanager.ui.component


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
import com.nudge.core.BLANK_STRING
import com.nudge.core.showCustomToast
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.surveymanager.R

@Composable
fun TypeMultiSelectedDropDownComponent(
    questionIndex: Int,
    title: String = BLANK_STRING,
    isMandatory: Boolean = false,
    isEditAllowed: Boolean = true,
    showCardView: Boolean = false,
    maxCustomHeight: Dp,
    content: List<ContentList?>? = listOf(),
    isFromTypeQuestion: Boolean = false,
    hintText: String = stringResource(R.string.select),
    sources: List<ValuesDto>,
    optionStateMap: Map<Pair<Int, Int>, Boolean> = emptyMap(),
    onDetailIconClicked: () -> Unit = {}, // Default empty lambda
    navigateToMediaPlayerScreen: (ContentList) -> Unit,
    onAnswerSelection: (selectValue: String) -> Unit,
) {
    val context = LocalContext.current
    val defaultSourceList = sources
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var selectedItems by remember {
        mutableStateOf(getSelectedOptionsValue(sources))
    }

    MultiSelectSelectDropDown(
        content = content,
        isFromTypeQuestion = isFromTypeQuestion,
        questionIndex = questionIndex,
        title = title,
        isMandatory = isMandatory,
        hint = hintText,
        items = defaultSourceList,
        modifier = Modifier.fillMaxWidth(),
        mTextFieldSize = textFieldSize,
        expanded = expanded,
        selectedItems = selectedItems,
        maxCustomHeight = maxCustomHeight,
        showCardView = showCardView,
        onDetailIconClicked = { onDetailIconClicked() },
        navigateToMediaPlayerScreen = { contentList ->
            navigateToMediaPlayerScreen(contentList)
        },
        onExpandedChange = {
            if (isEditAllowed) {
                expanded = !it
            } else {
                showCustomToast(
                    context,
                    context.getString(R.string.edit_disable_message)
                )
            }
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

fun getSelectedOptionsValue(values: List<ValuesDto>): List<String> {
    val selectedText = ArrayList<String>()
    values.forEach {
        if (it.isSelected == true) {
            selectedText.add(it.value)
        }
    }
    return selectedText

}