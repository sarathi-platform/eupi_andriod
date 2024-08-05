package com.nudge.incomeexpensemodule.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.incomeexpensemodule.R
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.commonUi.CustomOutlineTextField
import com.nudge.core.ui.commonUi.QuestionComponent
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.borderGrey
import com.nudge.core.ui.theme.dimen_60_dp
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.placeholderGrey
import com.nudge.core.ui.theme.smallTextStyle
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto

@Composable
fun <T> DropDownComponent(
    hint: String = stringResource(R.string.select),
    items: List<T>,
    title: String = BLANK_STRING,
    isMandatory: Boolean = false,
    modifier: Modifier,
    dropDownBorder: Color = borderGrey,
    dropDownBackground: Color = white,
    selectedItem: String = BLANK_STRING,
    expanded: Boolean = false,
    mTextFieldSize: Size,
    diableItem: Int = -1,
    onExpandedChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    onGlobalPositioned: (LayoutCoordinates) -> Unit,
    onItemSelected: (T) -> Unit,
) {
    // Up Icon when expanded and down icon when collapsed
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        if (title.isNotBlank()) {
            QuestionComponent(title = title, isRequiredField = isMandatory)
        }
        CustomOutlineTextField(
            value = selectedItem,
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
                    // This value is used to assign to
                    // the DropDown the same width
                    onGlobalPositioned(coordinates)
//                    mTextFieldSize = coordinates.size.toSize()
                },
            textStyle = newMediumTextStyle.copy(blueDark),
            singleLine = true,
            maxLines = 1,
            placeholder = {
                Text(text = hint, style = newMediumTextStyle, color = placeholderGrey)
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = blueDark,
                backgroundColor = Color.White,
                focusedIndicatorColor = borderGrey,
                unfocusedIndicatorColor = borderGrey,
            ),
            trailingIcon = {
                Icon(icon, "contentDescription",
                    Modifier.clickable { onExpandedChange(expanded) })
            }
        )

        // Create a drop-down menu with list of cities,
        // when clicked, set the Text Field text as the city selected
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onDismissRequest() },
            modifier = Modifier.width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
        ) {

            items.mapIndexed { index, item ->
                val title: String
                when (item) {
                    is ValuesDto -> {
                        title = item.value
                    }

                    else -> {
                        title = item.toString()
                    }
                }
                DropdownMenuItem(enabled = true, onClick = {
                    onItemSelected(item)
                }) {
                    Text(
                        text = title,
                        modifier = Modifier.fillMaxWidth(),
                        style = smallTextStyle.copy(
                            blueDark
                        )
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DropDownWithTittleCompoentPerview() {
    var casteTextFieldSize by remember { mutableStateOf(Size.Zero) }
    val screens = listOf(
        "Setting",
        "Question",
        "SingleQuestion",
        "DigitalFormA",
        "DigitalFormB",
        "DigitalFormC",
        "Login",
        "Other"
    )
    DropDownComponent(
        items = screens,
        modifier = Modifier.padding(10.dp),
        mTextFieldSize = casteTextFieldSize,
        onExpandedChange = { },
        onDismissRequest = { },
        onGlobalPositioned = {},
        onItemSelected = {},
    )
}