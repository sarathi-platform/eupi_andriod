package com.nudge.core.ui.commonUi.componet_.component

import android.app.DatePickerDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.DD_MMM_YYYY_FORMAT
import com.nudge.core.R
import com.nudge.core.ui.commonUi.QuestionComponent
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_60_dp
import com.nudge.core.ui.theme.greyColor
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.placeholderGrey
import com.nudge.core.ui.theme.smallerTextStyle
import com.nudge.core.ui.theme.white
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerComponent(
    title: String = BLANK_STRING,
    hintText: String = BLANK_STRING,
    defaultValue: String = BLANK_STRING,
    isMandatory: Boolean = false,
    isEditable: Boolean = true,
    onAnswerSelection: (selectValue: String) -> Unit,
) {
    var text by remember { mutableStateOf(defaultValue) }
    val context = LocalContext.current

    //TODO Anupam Test this code and fix crash in text.getDateInMillis(pattern = DD_MMM_YYYY_FORMAT).
    /*val scope = rememberCoroutineScope()

    val datePickerState =
        rememberCustomDatePickerState(initialSelectedDateMillis = text.getDateInMillis(pattern = DD_MMM_YYYY_FORMAT))

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
            val dateFormat =
                SimpleDateFormat(DD_MMM_YYYY_FORMAT, Locale.getDefault())
            val formattedDate = dateFormat.format(datePickerState.selectedDateMillis)
            text = formattedDate
            onAnswerSelection(text)
            datePickerDialogProperties.hide()
        }
    )*/

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimen_10_dp)
    ) {
        if (title.isNotBlank()) {
            QuestionComponent(title = title, isRequiredField = isMandatory)
        }

        /*OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .background(white)
                .clickable {
                    scope.launch {
                        datePickerDialogProperties.show()
                    }
                },
            value = text,
            enabled = isEditable,
            readOnly = true,
            textStyle = defaultTextStyle,
            singleLine = true,
            interactionSource = remember {
                MutableInteractionSource()
            }.also { interactionSource ->
                LaunchedEffect(key1 = interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            scope.launch {
                                datePickerDialogProperties.show()
                            }
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
            placeholder = {
                Text(
                    text = hintText,
                    style = smallerTextStyle.copy(
                        color = placeholderGrey
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentHeight(align = Alignment.CenterVertically)
                )
            },
            trailingIcon = {
                IconButton(onClick = {
                    scope.launch {
                        datePickerDialogProperties.show()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Calendar Icon",
                        tint = placeholderGrey
                    )
                }

            },
            onValueChange = {}
        )*/

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
                        style = smallerTextStyle.copy(
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
                        val calendar = Calendar.getInstance()
                        val year = calendar[Calendar.YEAR]
                        val month = calendar[Calendar.MONTH]
                        val day = calendar[Calendar.DAY_OF_MONTH]
                        DatePickerDialog(
                            context,
                            R.style.my_dialog_theme,
                            { _, selectedYear, selectedMonth, selectedDay ->
                                val calendar = Calendar.getInstance()
                                calendar.set(selectedYear, selectedMonth, selectedDay)
                                val dateFormat =
                                    SimpleDateFormat(DD_MMM_YYYY_FORMAT, Locale.ENGLISH)
                                val formattedDate = dateFormat.format(calendar.time)
                                text = formattedDate
                                onAnswerSelection(text)
                            }, year, month, day
                        ).show()
                    },
            )
        }

    }
}
