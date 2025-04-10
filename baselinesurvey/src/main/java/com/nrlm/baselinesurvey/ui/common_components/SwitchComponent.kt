package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.ui.theme.black100Percent
import com.nrlm.baselinesurvey.ui.theme.greenOnline
import com.nrlm.baselinesurvey.ui.theme.largeTextStyle
import com.nrlm.baselinesurvey.ui.theme.switchColor
import com.nrlm.baselinesurvey.ui.theme.white

@Composable
fun SwitchComponent(
    title: String?,
    defaultValue: String = "No",
    onAnswerSelection: (selectValue: String) -> Unit
) {
    var defaultChecked = defaultValue.equals("Yes", false)
    var checked by remember { mutableStateOf(defaultChecked) }
    Row {
        Text(
            modifier = Modifier.weight(0.7F),
            text = title ?: "Sectect",
            textAlign = TextAlign.Start,
            style = largeTextStyle,
            color = black100Percent
        )
        Switch(
            checked = checked,
            onCheckedChange = {
                checked = it
                val selectedValue = if (checked) "Yes" else "No"
                onAnswerSelection(selectedValue)
            },
            modifier = Modifier
                .padding(1.dp)
                .weight(0.3f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = white,
                checkedTrackColor = if (checked) greenOnline else switchColor,
                uncheckedThumbColor = white
            ),
        )
        Text(
            text = if (checked) "Yes" else "No",
            textAlign = TextAlign.Center,
            style = largeTextStyle,
            color = black100Percent
        )

    }
}

@Preview(showBackground = true)
@Composable
fun SwitchComponentPreview() {
    SwitchComponent("Married") {}
}