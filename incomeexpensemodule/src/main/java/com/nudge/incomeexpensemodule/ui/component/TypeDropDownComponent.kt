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
import androidx.compose.ui.unit.toSize
import com.nudge.core.BLANK_STRING
import com.nudge.core.R
import com.nudge.core.showCustomToast
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto

@Composable
fun TypeDropDownComponent(
    title: String = BLANK_STRING,
    hintText: String = stringResource(R.string.select),
    sources: List<ValuesDto>?,
    isMandatory: Boolean = false,
    isEditAllowed: Boolean = true,
    diableItem: Int = -1,
    selectedValue: String? = null,
    onAnswerSelection: (selectedValuesDto: ValuesDto) -> Unit
) {
    val context = LocalContext.current
    val defaultSourceList =
        sources ?: listOf(ValuesDto(id = 1, "Yes"), ValuesDto(id = 2, "No"))
    var expanded by remember { mutableStateOf(false) }
//    var selectedOptionText by remember(defaultSourceList.size) {
//        mutableStateOf(
//            defaultSourceList.find { it.isSelected == true }?.value
//                ?: hintText
//        )
    //   }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }


    DropDownComponent(items = defaultSourceList,
        modifier = Modifier.fillMaxWidth(),
        mTextFieldSize = textFieldSize,
        expanded = expanded,
        title = title,
        diableItem = diableItem,
        isMandatory = isMandatory,
        selectedItem = selectedValue ?: hintText,
        isEditAllowed = isEditAllowed,
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
        onItemSelected = {
//            selectedOptionText =
//                defaultSourceList[defaultSourceList.indexOf(it)].value
            onAnswerSelection(defaultSourceList[defaultSourceList.indexOf(it)])
            expanded = false

        })

}