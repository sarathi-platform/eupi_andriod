package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.model.datamodel.ValuesDto
import com.nrlm.baselinesurvey.ui.theme.NotoSans
import com.nrlm.baselinesurvey.ui.theme.blueDark
import com.nrlm.baselinesurvey.ui.theme.borderGrey
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.newMediumTextStyle
import com.nrlm.baselinesurvey.ui.theme.placeholderGrey
import com.nrlm.baselinesurvey.ui.theme.red
import com.nrlm.baselinesurvey.ui.theme.textColorDark
import com.nrlm.baselinesurvey.ui.theme.white


//TODO handle everything using id
@Composable
fun MultiSelectDropdown(
    items: List<ValuesDto>,
    selectedItems: List<String>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isContent: Boolean = false,
    title: String = stringResource(id = R.string.select),
    hint: String = stringResource(id = R.string.select),
    dropDownBorder: Color = borderGrey,
    dropDownBackground: Color = white,
    isRequiredField: Boolean = false,
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    onGlobalPositioned: (LayoutCoordinates) -> Unit,
    mTextFieldSize: Size,
    onInfoButtonClicked: () -> Unit,
) {
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        if (title.isNotBlank()) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.fillMaxWidth(.9f),
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = textColorDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = NotoSans
                            )
                        ) {
                            append(title)
                        }
                        if (isRequiredField) {
                            withStyle(
                                style = SpanStyle(
                                    color = red,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = NotoSans
                                )
                            ) {
                                append("*")
                            }
                        }
                    }
                )
                if (isContent) {
                    Spacer(modifier = Modifier.size(dimen_8_dp))
                    Icon(
                        painter = painterResource(id = R.drawable.info_icon),
                        contentDescription = "question info button",
                        Modifier
                            .size(dimen_18_dp)
                            .clickable {
                                onInfoButtonClicked()
                            },

                        tint = blueDark
                    )
                }
            }
        }
        val txt = if (selectedItems.isNotEmpty()) {
            selectedItems.joinToString(", ")
        } else {
            "Select"
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
                .padding(top = 6.dp)
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
                                onItemSelected(item.value)
                            }
                    )
                }

            }
        }
    }

}
