package com.nudge.core.ui.commonUi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.nudge.core.BLANK_STRING
import com.nudge.core.R
import com.nudge.core.ui.theme.dateRangeFieldColor
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.otpBorderColor
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white

@Composable
fun CustomDateRangePickerDisplay(
    modifier: Modifier = Modifier.padding(horizontal = dimen_8_dp),
    value: String = BLANK_STRING,
    label: String,
    onViewClicked: () -> Unit,
) {

    Row(
        Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .background(white)
                .weight(1f)
                .clickable {
                    onViewClicked()
                },
            value = value,
            enabled = true,
            readOnly = true,
            textStyle = defaultTextStyle,
            singleLine = true,
            interactionSource = remember {
                MutableInteractionSource()
            }.also { interactionSource ->
                LaunchedEffect(key1 = interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            onViewClicked()
                        }
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textColorDark,
                unfocusedBorderColor = dateRangeFieldColor,
                focusedBorderColor = dateRangeFieldColor,
                unfocusedContainerColor = white,
                focusedContainerColor = white,
            ),
            label = {
                Text(
                    text = label,
                    color = otpBorderColor
                )
            },
            placeholder = {
                Text(
                    text = label,
                    color = otpBorderColor
                )
            },
            trailingIcon = {
                IconButton(onClick = {
                    onViewClicked()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "Date Range"
                    )
                }

            },
            onValueChange = {}
        )
    }
}