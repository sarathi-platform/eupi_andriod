package com.sarathi.surveymanager.ui.component


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
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
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.theme.defaultCardElevation
import com.nudge.core.ui.theme.dimen_0_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.surveymanager.R

@Composable
fun TypeDropDownComponent(
    title: String = BLANK_STRING,
    hintText: String = stringResource(R.string.select),
    sources: List<ValuesDto>?,
    isMandatory: Boolean = false,
    isEditAllowed: Boolean = true,
    showInsideCard: Boolean = false,
    questionNumber: String = BLANK_STRING,
    onAnswerSelection: (selectedValuesDto: ValuesDto) -> Unit
) {
    val context = LocalContext.current
    val defaultSourceList =
        sources ?: listOf(ValuesDto(id = 1, "Yes"), ValuesDto(id = 2, "No"))
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember {
        mutableStateOf(
            defaultSourceList.find { it.isSelected == true }?.value
                ?: hintText
        )
    }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    BasicCardView(
        cardElevation = CardDefaults.cardElevation(
            defaultElevation = if (showInsideCard) defaultCardElevation else dimen_0_dp
        )
    ) {
        DropDownComponent(items = defaultSourceList,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (showInsideCard) dimen_16_dp else dimen_0_dp),
            mTextFieldSize = textFieldSize,
            expanded = expanded,
            title = title,
            questionNumber = questionNumber,
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
                    defaultSourceList[defaultSourceList.indexOf(it)].value
                onAnswerSelection(defaultSourceList[defaultSourceList.indexOf(it)])
                expanded = false

            }
        )
    }


}