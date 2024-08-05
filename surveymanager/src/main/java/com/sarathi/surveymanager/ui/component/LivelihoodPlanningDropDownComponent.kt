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
import androidx.compose.ui.unit.toSize
import com.nudge.core.showCustomToast
import com.nudge.core.value
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodEntity
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.LivelihoodDropDownUiModel
import com.sarathi.surveymanager.R

@Composable
fun LivelihoodPlanningDropDownComponent(
    title: String = BLANK_STRING,
    hintText: String = stringResource(R.string.select),
    sources: List<LivelihoodDropDownUiModel>?,
    isMandatory: Boolean = false,
    isEditAllowed: Boolean = true,
    diableItem: Int = -1,
    onAnswerSelection: (livelihoodUIEntity: LivelihoodDropDownUiModel) -> Unit
) {
    val context = LocalContext.current
    val defaultSourceList =
        sources ?: listOf()
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember {
        mutableStateOf("")
//        mutableStateOf(
//            defaultSourceList.find { it.isSelected }?.livelihoodEntity?.name.value()
//
//        )
    }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }


    DropDownComponent<LivelihoodDropDownUiModel>(items = defaultSourceList,
        modifier = Modifier.fillMaxWidth(),
        mTextFieldSize = textFieldSize,
        expanded = expanded,
        title = title,
        diableItem = diableItem,
        isMandatory = isMandatory,
        selectedItem = selectedOptionText,
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
            selectedOptionText =
                defaultSourceList[defaultSourceList.indexOf(it)].name
//            selectedOptionText =
//                defaultSourceList[defaultSourceList.indexOf(it)].livelihoodEntity.name.value()
            onAnswerSelection(defaultSourceList[defaultSourceList.indexOf(it)])
            expanded = false

        })

}

data class LivelihoodUIEntity(val livelihoodEntity: LivelihoodEntity, val isSelected: Boolean)