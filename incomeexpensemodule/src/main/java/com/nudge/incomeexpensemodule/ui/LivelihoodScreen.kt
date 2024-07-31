package com.nudge.incomeexpensemodule.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nudge.core.ui.theme.assetValueIconColor

@Composable
fun LivelihoodScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        HeaderSection()
        Spacer(modifier = Modifier.height(16.dp))
        EventsList()
        Spacer(modifier = Modifier.height(16.dp))
        ShowMoreButton()
        Spacer(modifier = Modifier.height(16.dp))

    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF3E0), shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "Income", fontSize = 18.sp, color = Color.Gray)
            Text(text = "₹ 2000", fontSize = 18.sp, color = Color.Black)
        }
        Column {
            Text(text = "Expense", fontSize = 18.sp, color = Color.Gray)
            Text(text = "₹ 500", fontSize = 18.sp, color = Color.Black)
        }
        Column {
            Text(text = "Asset Value", fontSize = 18.sp, color = Color.Gray)
            Text(text = "₹ 4000", fontSize = 18.sp, color = Color.Black)
        }
    }
}

@Composable
fun EventsList() {
    Column {
        EventItem("Asset Purchase", "₹ 8000", "+2", "15 Jan'24", Color.Red)
        Spacer(modifier = Modifier.height(8.dp))
        EventItem("Feed Procurement", "₹ 1000", "", "10 Jan'24", Color.Red)
        Spacer(modifier = Modifier.height(8.dp))
        EventItem("Birth", "", "+1", "9 Jan'24", Color.Black)
    }
}

@Composable
fun EventItem(event: String, amount: String, assets: String, date: String, amountColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = event, fontSize = 18.sp, color = Color.Black)
            Text(text = "Amount: $amount", fontSize = 18.sp, color = amountColor)
            Text(text = "Assets: $assets", fontSize = 18.sp, color = Color.Black)
        }
        Text(text = date, fontSize = 18.sp, color = Color.Gray)
    }
}

@Composable
fun ShowMoreButton() {
    TextButton(
        onClick = { },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Show more",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = assetValueIconColor
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LivelihoodScreen()
}