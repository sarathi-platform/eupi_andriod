package com.nudge.incomeexpensemodule.ui.edit_history_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.dividerColor
import com.nudge.core.ui.theme.grayColor
import com.nudge.core.ui.theme.incomeCardTopViewColor
import com.nudge.core.ui.theme.redIconColor
import com.nudge.core.ui.theme.smallTextStyle
import com.nudge.core.ui.theme.white

@Composable
fun EventItem(event: Event, isDeleted: Boolean) {
    Row(
        modifier = Modifier
            .background(Color.Transparent)

    ) {
        // Event details
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            BasicCardView(
                colors = CardDefaults.cardColors(
                    containerColor = white
                ),
                modifier = Modifier.padding(horizontal = dimen_10_dp)
            ) {
                if (isDeleted) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(incomeCardTopViewColor)
                    ) {
                        Spacer(modifier = Modifier.weight(1.0f))
                        Text(
                            modifier = Modifier.padding(horizontal = dimen_5_dp),
                            text = "Delete",
                            style = smallTextStyle.copy(redIconColor)
                        )
                    }
                }
                Column(modifier = Modifier.padding(dimen_10_dp)) {
                    TextRowView(
                        text1 = "Event:",
                        text2 = " ${event.eventType}",
                        text3 = event.eventDate
                    )
                    Divider()
                    Spacer(modifier = Modifier.height(4.dp))
                    TextRowView(
                        text1 = "Asset Type:",
                        text2 = " ${event.assetType}",
                        text3 = "₹ ${event.amount}"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextRowView(text1 = "Increase in Number:", text2 = " ${event.increaseInNumber}")
                }
            }
        }

        // Dot and vertical line on the right
        Column(
            modifier = Modifier.padding(end = dimen_5_dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            SolidCircleWithBorder(
                circleColor = if (!isDeleted) Color(0xFF007AFF) else Color.Transparent,
                borderColor = if (!isDeleted) Color(0xFF007AFF) else grayColor,
                circleDiameter = 10,
                borderWidth = 2f
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (!isDeleted) {
                for (i in 1..12) {
                    androidx.compose.material.Divider(
                        color = dividerColor,
                        modifier = Modifier
                            .height(dimen_8_dp)
                            .width(dimen_1_dp)
                            .padding(vertical = dimen_2_dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TextRowView(
    text1: String? = null,
    text2: String? = null,
    text3: String? = null,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        text1?.let {
            Text(
                text = it,
                style = defaultTextStyle.copy(grayColor)
            )
        }
        text2?.let {
            Text(
                text = it,
                style = defaultTextStyle.copy(blueDark)
            )
        }
        Spacer(modifier = Modifier.weight(1.0f))
        text3?.let {
            Text(
                text = it,
                style = defaultTextStyle.copy(blueDark)
            )
        }
    }
}

@Composable
fun EventList(events: List<Event>) {
    Column {
        // DateRangeSelector()
        events.forEach { event ->
            EventItem(event = event, isDeleted = event.isDeleted)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DateRangeSelector() {
    Row(modifier = Modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .background(Color.LightGray)
                .padding(8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = "From",
                onValueChange = {},
                textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                cursorBrush = SolidColor(Color.Black)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .background(Color.LightGray)
                .padding(8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = "To",
                onValueChange = {},
                textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                cursorBrush = SolidColor(Color.Black)
            )
        }
    }
}

@Composable
fun MainScreen() {
    Column {
        //DateRangeSelector()
        Spacer(modifier = Modifier.height(16.dp))
        EventList(
            events = listOf(
                Event(
                    "Asset Purchase",
                    "Adult Male",
                    2000,
                    2,
                    "12 Jan, 2024",
                    isDeleted = false,
                    eventTime = "15 Jan,13:00"
                ),
                Event(
                    "Asset Purchase",
                    "Adult Male",
                    1000,
                    1,
                    "12 Jan, 2024",
                    isDeleted = false,
                    eventTime = "14 Jan,10:00"
                ),
                Event(
                    "Asset Purchase",
                    "Adult Male",
                    -1000,
                    1,
                    "12 Jan, 2024",
                    isDeleted = false,
                    eventTime = "11 Jan, 09:00"
                ),
                Event(
                    "Asset Purchase",
                    "Adult Male",
                    2000,
                    2,
                    "12 Jan, 2024",
                    isDeleted = true,
                    eventTime = "10 Jan, 10:02"
                )
            )
        )
    }
}

data class Event(
    val eventType: String,
    val assetType: String,
    val amount: Int,
    val increaseInNumber: Int,
    val eventDate: String,
    val isDeleted: Boolean,
    val eventTime: String
)

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun MyApp() {
    MaterialTheme {
        MainScreen()
    }
}
