package com.nudge.core.ui.commonUi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_60_dp
import com.nudge.core.ui.theme.greyColor
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.placeholderGrey
import com.nudge.core.ui.theme.smallTextStyle
import com.nudge.core.ui.theme.white

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerTextFieldComponent(
    title: String = BLANK_STRING,
    hintText: String = BLANK_STRING,
    defaultValue: String = BLANK_STRING,
    isMandatory: Boolean = false,
    isEditable: Boolean = true,
    onDateSelected: (selectedDate: Long?) -> Unit,
) {

    var text by remember(defaultValue) { mutableStateOf(defaultValue) }

    val datePickerState =
        rememberCustomDatePickerState()

    val datePickerProperties = rememberDatePickerProperties(
        state = datePickerState
    )

    val datePickerDialogProperties = rememberCustomDatePickerDialogProperties()


    CustomDatePickerComponent(
        datePickerProperties = datePickerProperties,
        datePickerDialogProperties = datePickerDialogProperties,
        onDismissRequest = {
            datePickerDialogProperties.hide()
        },
        onConfirmButtonClicked = {
            onDateSelected(datePickerState.selectedDateMillis)
            datePickerDialogProperties.hide()
        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimen_10_dp)
    ) {

        if (title.isNotBlank()) {
            QuestionComponent(title = title, isRequiredField = isMandatory)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            TextField(
                value = text,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimen_60_dp)
                    .background(white, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, greyColor, shape = RoundedCornerShape(8.dp)),
                onValueChange = { text = it },
                textStyle = newMediumTextStyle.copy(blueDark),
                placeholder = {
                    Text(
                        text = hintText,
                        style = smallTextStyle.copy(
                            color = placeholderGrey
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentHeight(align = Alignment.CenterVertically)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Calendar Icon",
                            tint = placeholderGrey
                        )
                    }
                },
                enabled = isEditable,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
                    .clickable(enabled = isEditable) {
                        datePickerDialogProperties.show()
                    },
            )
        }

    }

}