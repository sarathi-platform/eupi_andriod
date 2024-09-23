package com.nudge.core.ui.commonUi


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nudge.core.ui.theme.GreyDark
import com.nudge.core.ui.theme.defaultTextStyle


@Composable
fun StrikethroughText(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = defaultTextStyle,
    strikethroughColor: Color = GreyDark,
    isStrikethrough: Boolean = false
) {
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }


    Box(contentAlignment = Alignment.Center) {
        Text(
            modifier = modifier,
            text = text,
            style = textStyle,
            onTextLayout = { layoutResult ->
                textLayoutResult = layoutResult
            },
            textAlign = TextAlign.Center,
        )
        if (isStrikethrough && textLayoutResult != null) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
            ) {
                val textHeight = textLayoutResult!!.size.height.toFloat()
                val baseline = textLayoutResult!!.firstBaseline.toFloat()
                val textWidth = size.width

                // Draw the strikethrough line
                drawLine(
                    color = strikethroughColor,
                    start = Offset(0f, baseline - (textHeight / 6)),
                    end = Offset(textWidth, baseline - (textHeight / 6)),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StrikethroughTextPreview() {
    val condition = remember { mutableStateOf(true) } // Example state to toggle strikethrough

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Toggle button for demonstration
        Button(onClick = { condition.value = !condition.value }) {
            Text("Toggle Strikethrough")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Text with conditional strikethrough
        StrikethroughText(
            text = "This text has a conditional strikethrough.",
            strikethroughColor = Color.Red,
            isStrikethrough = condition.value
        )
    }
}
