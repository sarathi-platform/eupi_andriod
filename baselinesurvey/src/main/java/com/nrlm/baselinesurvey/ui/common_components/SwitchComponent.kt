package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.ui.theme.black100Percent
import com.nrlm.baselinesurvey.ui.theme.greenOnline
import com.nrlm.baselinesurvey.ui.theme.largeTextStyle
import com.nrlm.baselinesurvey.ui.theme.switchColor
import com.nrlm.baselinesurvey.ui.theme.white

@Preview(showBackground = true)
@Composable
fun SwitchComponent() {
    var checked by remember { mutableStateOf(true) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Text(
            modifier = Modifier.weight(0.7F),
            text = "Married",
            textAlign = TextAlign.Start,
            style = largeTextStyle,
            color = black100Percent
        )
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            modifier = Modifier
                .padding(10.dp)
                .weight(0.3f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = white,
                checkedTrackColor = if (checked) greenOnline else switchColor,
                uncheckedThumbColor = white
            )
        )
        Text(
            text = if (checked) "Yes" else "No",
            textAlign = TextAlign.Center,
            style = largeTextStyle,
            color = black100Percent
        )

    }


}