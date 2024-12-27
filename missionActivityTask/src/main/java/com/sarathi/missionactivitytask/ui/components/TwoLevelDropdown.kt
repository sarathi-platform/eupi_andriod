package com.sarathi.missionactivitytask.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.toSize
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.borderGrey
import com.nudge.core.ui.theme.dimen_60_dp
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.placeholderGrey
import com.nudge.core.ui.theme.smallTextStyle
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.ui.component.QuestionComponent

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun TwoLevelDropdownPreview() {
    TwoLevelDropdown(
        modifier = Modifier.padding(10.dp)
    )
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun TwoLevelDropdown(
    modifier: Modifier = Modifier,
    hint: String = stringResource(R.string.select),
) {
    val categories = listOf("All", "Fruits", "Vegetables")
    val subcategoriesMap = mapOf(
        "Fruits" to listOf("All", "Apple", "Banana", "Orange"),
        "Vegetables" to listOf("All", "Carrot", "Broccoli", "Spinach")
    )

    var selectedCategory = remember { mutableStateOf<String?>("") }
    var selectedSubcategory = remember { mutableStateOf<String?>("") }

    var expanded_1 = remember { mutableStateOf(false) }
    var expanded_2 = remember { mutableStateOf(false) }
    var textFieldSize_1 = remember { mutableStateOf(Size.Zero) }
    var textFieldSize_2 = remember { mutableStateOf(Size.Zero) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        DropDown(
            title = "Select Category",
            selectedItem = selectedCategory.value ?: "",
            onExpandedChange = {
                expanded_2.value = false
                expanded_1.value = !expanded_1.value
            },
            expanded = expanded_1.value,
            hint = hint,
            onDismissRequest = { expanded_1.value = false },
            items = categories,
            mTextFieldSize = textFieldSize_1.value,
            onGlobalPositioned = { coordinates ->
                textFieldSize_1.value = coordinates.size.toSize()
            }
        ) { category ->
            selectedCategory.value = category
            selectedSubcategory.value = null // Reset subcategory when category changes
            expanded_1.value = false
        }

        Spacer(modifier = Modifier.padding(8.dp))

        if (!selectedCategory.value.isNullOrEmpty() && selectedCategory.value != "All") {
            DropDown(
                title = "Select Subcategory",
                selectedItem = selectedSubcategory.value ?: "",
                onExpandedChange = {
                    expanded_1.value = false
                    expanded_2.value = !expanded_2.value
                },
                expanded = expanded_2.value,
                hint = hint,
                onDismissRequest = { expanded_2.value = false },
                items = subcategoriesMap[selectedCategory.value] ?: emptyList(),
                mTextFieldSize = textFieldSize_2.value,
                onGlobalPositioned = { coordinates ->
                    textFieldSize_2.value = coordinates.size.toSize()
                }
            ) { subcategory ->
                selectedSubcategory.value = subcategory
                expanded_2.value = false
            }
        }
    }
}

@Composable
private fun <T> DropDown(
    title: String,
    selectedItem: String,
    onExpandedChange: (Boolean) -> Unit,
    expanded: Boolean,
    hint: String,
    onDismissRequest: () -> Unit,
    items: List<T>,
    mTextFieldSize: Size,
    onGlobalPositioned: (LayoutCoordinates) -> Unit,
    onClick: (String) -> Unit
) {
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    if (title.isNotBlank()) {
        QuestionComponent(title = title)
    }

    CustomOutlineTextField(
        value = selectedItem,
        onValueChange = {},
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            onExpandedChange(!expanded) // Toggle the dropdown state
                        }
                    }
                }
            },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(dimen_60_dp)
            .clickable { onExpandedChange(!expanded) } // Toggle on click
            .onGloballyPositioned(onGlobalPositioned),
        textStyle = newMediumTextStyle.copy(blueDark),
        singleLine = true,
        maxLines = 1,
        placeholder = {
            androidx.compose.material.Text(
                text = hint,
                style = newMediumTextStyle,
                color = placeholderGrey
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            textColor = blueDark,
            backgroundColor = Color.White,
            focusedIndicatorColor = borderGrey,
            unfocusedIndicatorColor = borderGrey,
        ),
        trailingIcon = {
            Icon(
                icon,
                contentDescription = null,
                Modifier.clickable { onExpandedChange(!expanded) })
        }
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .width(with(LocalDensity.current) {
                mTextFieldSize.width.toDp()
            })
            .onGloballyPositioned { layoutCoordinates ->
                onGlobalPositioned(layoutCoordinates)
            }
    ) {
        items.forEach { item ->
            val title = when (item) {
                is ValuesDto -> item.value
                else -> item.toString()
            }
            androidx.compose.material.DropdownMenuItem(onClick = { onClick(title) }) {
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    style = smallTextStyle.copy(blueDark)
                )
            }
        }
    }
}
