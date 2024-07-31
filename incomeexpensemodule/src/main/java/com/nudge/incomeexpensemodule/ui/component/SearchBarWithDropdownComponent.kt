package com.nudge.incomeexpensemodule.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.borderGrey
import com.nudge.core.ui.theme.dimen_60_dp
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.placeholderGrey
import com.sarathi.surveymanager.ui.component.CustomOutlineTextField

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchBarWithDropdownComponent(
    expanded: Boolean = false,
    mTextFieldSize: Size,
    onExpandedChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    onGlobalPositioned: (LayoutCoordinates) -> Unit,
    onItemSelected: () -> Unit,
) {
    var selectedEvent by remember { mutableStateOf("Asset Purchase") }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val events = listOf("Birth", "Death", "Gift", "Purchase", "Sale", "Feed Procurement", "Health")
    val filteredEvents = events.filter { it.contains(searchQuery.text, ignoreCase = true) }
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    var textFieldWidth by remember { mutableStateOf(0.dp) }
    val currentDensity = LocalDensity.current

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Event*", fontSize = 16.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        CustomOutlineTextField(
            value = selectedEvent,
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
                    textFieldWidth = with(currentDensity) { coordinates.size.width.toDp() }

                },
            textStyle = newMediumTextStyle.copy(blueDark),
            singleLine = true,
            maxLines = 1,
            placeholder = {
                Text(text = "hint", style = newMediumTextStyle, color = placeholderGrey)
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
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                onItemSelected()//expanded = !expanded
            },
            modifier = Modifier.width(textFieldWidth)
        ) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onDismissRequest() },
                modifier = Modifier.width(textFieldWidth) // Ensure width matches parent
            ) {
                Column {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) { innerTextField ->
                        if (searchQuery.text.isEmpty()) {
                            Text(
                                text = "Search",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                    Divider()
                    filteredEvents.forEach { event ->
                        DropdownMenuItem(
                            onClick = {
                                onItemSelected()
                                selectedEvent = event
                            }
                        ) {
                            Text(text = event)
                        }
                    }
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true)
fun PerviewSerch() {
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var expanded by remember { mutableStateOf(false) }

    SearchBarWithDropdownComponent(
        expanded = expanded,
        mTextFieldSize = textFieldSize,
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
            expanded = !expanded

        }
    )
}