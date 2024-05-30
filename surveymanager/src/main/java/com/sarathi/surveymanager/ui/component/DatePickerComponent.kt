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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sarathi.surveymanager.theme.buttonTextStyle
import com.sarathi.surveymanager.theme.greyColor
import com.sarathi.surveymanager.theme.placeholderGrey
import com.sarathi.surveymanager.theme.white
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
fun DatePickerComponent() {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)) {
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
                    "DD/MM//YYYY",
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