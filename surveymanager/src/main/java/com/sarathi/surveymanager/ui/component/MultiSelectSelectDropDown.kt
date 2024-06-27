package com.sarathi.surveymanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.borderGrey
import com.nudge.core.ui.theme.dimen_60_dp
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.placeholderGrey
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.surveymanager.R


@Composable
fun MultiSelectSelectDropDown(
    title: String = BLANK_STRING,
    isMandatory: Boolean = false,
    items: List<ValuesDto>,
    selectedItems: List<String>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = stringResource(R.string.select),
    dropDownBorder: Color = borderGrey,
    dropDownBackground: Color = white,
    isRequiredField: Boolean = false,
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    onGlobalPositioned: (LayoutCoordinates) -> Unit,
    mTextFieldSize: Size,
) {
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {

        val txt = if (selectedItems.isNotEmpty()) {
            selectedItems.joinToString(", ")
        } else {
            stringResource(R.string.select)
        }
        if (title.isNotBlank()) {
            QuestionComponent(title = title, isRequiredField = isMandatory)
        }
        CustomOutlineTextField(
            value = txt,
            onValueChange = {
            },
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                onExpandedChange(expanded)
                            }
                        }
                    }
                },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimen_60_dp)
                .clickable { onExpandedChange(expanded) }
                .onGloballyPositioned { coordinates ->
                    onGlobalPositioned(coordinates)
                },
            textStyle = newMediumTextStyle,
            singleLine = true,
            maxLines = 1,
            placeholder = {
                Text(text = hint, style = newMediumTextStyle, color = placeholderGrey)
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = textColorDark,
                backgroundColor = Color.White,
                focusedIndicatorColor = borderGrey,
                unfocusedIndicatorColor = borderGrey,
            ),
            trailingIcon = {
                Icon(icon, "contentDescription",
                    Modifier.clickable { onExpandedChange(expanded) })
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onDismissRequest() },
            modifier = Modifier
                .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
                .background(
                    white
                )
        ) {

            items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedItems.contains(item.value),
                        onCheckedChange = {
                            onItemSelected(item.value)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = blueDark,
                            uncheckedColor = Color.Gray,
                            checkmarkColor = Color.White
                        ),
                    )
                    Text(
                        text = item.value,
                        style = newMediumTextStyle,
                        textAlign = TextAlign.Start,
                        color = if (selectedItems.contains(item.value)) blueDark else Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onItemSelected(item.value.toString())
                            }
                    )
                }
            }
        }
    }
}
