package com.nudge.incomeexpensemodule.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.incomeexpensemodule.R
import com.nudge.core.ui.theme.blueDark

@Composable
fun DateRangePickerComponent() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        DatePicker(label = "From")
        Spacer(modifier = Modifier.width(16.dp))
        DatePicker(label = "To")
    }
}

@Composable
fun DatePicker(label: String) {
    Column {
        Text(text = label, fontSize = 14.sp)
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Select") },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_download_icon),
                    contentDescription = null,
                    tint = blueDark,
                )
            },
            modifier = Modifier.width(150.dp)
        )
    }
}