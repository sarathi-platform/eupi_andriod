package com.nrlm.baselinesurvey.ui.question_type_screen.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.toSize
import com.nrlm.baselinesurvey.ui.common_components.DropDownWithTitleComponent

@Composable
fun TypeDropDownComponent() {
    val source = listOf(
        "Agriculture",
        "Question",
        "SingleQuestion",
        "DigitalFormA",
        "DigitalFormB",
        "DigitalFormC",
        "Login",
        "Other"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(source[0]) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    DropDownWithTitleComponent(
        title = "Source",
        items = source,
        modifier = Modifier.fillMaxWidth(),
        mTextFieldSize = textFieldSize,
        expanded = expanded,
        selectedItem = selectedOptionText,
        onExpandedChange = {
            expanded = !it
        },
        onDismissRequest = {
            expanded = false
        },
        onGlobalPositioned = { coordinates ->
            textFieldSize = coordinates.size.toSize()
        },
        onItemSelected = {
            selectedOptionText = source[source.indexOf(it)]
            expanded = false
        },
    )
}