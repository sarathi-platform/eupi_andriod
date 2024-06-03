package com.sarathi.surveymanager.ui.component

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nudge.core.ui.events.theme.buttonTextStyle
import com.nudge.core.ui.events.theme.greyColor
import com.nudge.core.ui.events.theme.placeholderGrey
import com.nudge.core.ui.events.theme.white
import com.sarathi.dataloadingmangement.BLANK_STRING
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerComponent(
    title: String = BLANK_STRING,
    hintText: String = BLANK_STRING,
    onAnswerSelection: (selectValue: String) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        if (title?.isNotBlank() == true) {
            QuestionComponent(title = title, isRequiredField = true)
        }
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .background(white, shape = RoundedCornerShape(8.dp))
                .border(1.dp, greyColor, shape = RoundedCornerShape(8.dp))
                .padding(8.dp),
            value = text,
            onValueChange = { text = it },
            placeholder = {
                Text(
                    hintText,
                    style = buttonTextStyle.copy(color = placeholderGrey)
                )
            },
            trailingIcon = {
                IconButton(onClick = {
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                    DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
                        text = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                        onAnswerSelection(text)
                    }, year, month, day).show()
                }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Calendar Icon",
                        tint = placeholderGrey
                    )
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White, // Background color
                focusedIndicatorColor = Color.Transparent, // No underline when focused
                unfocusedIndicatorColor = Color.Transparent // No underline when not focused
            ),

            )
    }
}