package com.sarathi.surveymanager.ui.component


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_2_dp
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

    DropDownComponent(items = defaultSourceList,
        modifier = Modifier.fillMaxWidth(),
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

@Composable
fun TypeDropDownWithCardComponent(
    title: String = BLANK_STRING,
    hintText: String = stringResource(R.string.select),
    sources: List<ValuesDto>?,
    isMandatory: Boolean = false,
    isEditAllowed: Boolean = true,
    questionNumber: String = BLANK_STRING,
    onAnswerSelection: (selectedValuesDto: ValuesDto) -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(dimen_2_dp)
    ) {
        BasicCardView(

        ) {

            Box(
                modifier = Modifier
                    .padding(dimen_16_dp)
            ) {
                TypeDropDownComponent(
                    title = title,
                    hintText = hintText,
                    sources = sources,
                    isMandatory = isMandatory,
                    isEditAllowed = isEditAllowed,
                    questionNumber = questionNumber,
                    onAnswerSelection = { selectedValuesDto ->
                        onAnswerSelection(selectedValuesDto)
                    }
                )
            }

        }
        CustomVerticalSpacer()
    }

}


@Composable
fun TypeDropDownComponent(
    title: String = BLANK_STRING,
    hintText: String = stringResource(R.string.select),
    sources: List<ValuesDto>?,
    isMandatory: Boolean = false,
    showQuestionInCard: Boolean = false,
    isEditAllowed: Boolean = true,
    questionNumber: String = BLANK_STRING,
    onAnswerSelection: (selectedValuesDto: ValuesDto) -> Unit
) {

    if (showQuestionInCard) {
        TypeDropDownWithCardComponent(
            title = title,
            hintText = hintText,
            sources = sources,
            isMandatory = isMandatory,
            isEditAllowed = isEditAllowed,
            questionNumber = questionNumber,
            onAnswerSelection = { selectedValuesDto ->
                onAnswerSelection(selectedValuesDto)
            }
        )
    } else {
        TypeDropDownComponent(
            title = title,
            hintText = hintText,
            sources = sources,
            isMandatory = isMandatory,
            isEditAllowed = isEditAllowed,
            questionNumber = questionNumber,
            onAnswerSelection = { selectedValuesDto ->
                onAnswerSelection(selectedValuesDto)
            }
        )
    }

}