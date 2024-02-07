package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.ui.theme.borderGrey
import com.nrlm.baselinesurvey.ui.theme.defaultTextStyle
import com.nrlm.baselinesurvey.ui.theme.textColorDark

@Composable
fun EditTextWithTitleComponent(
    title: String? = "select",
    defaultValue: String = BLANK_STRING,
    onAnswerSelection: (selectValue: String) -> Unit,
) {
    val txt: MutableState<String> = remember {
        mutableStateOf(defaultValue)
    }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 2.dp)) {
        Text(
            text = title ?: "select",
            style = defaultTextStyle,
            color = textColorDark
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            value = txt.value,
            onValueChange = {
                txt.value = it
                onAnswerSelection(txt.value)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = borderGrey,
                textColor = textColorDark
            )
        )

    }
}

@Composable
@Preview(showBackground = true)
fun EditTextWithTitleComponentPreview() {
    EditTextWithTitleComponent(title = "select") {}
}