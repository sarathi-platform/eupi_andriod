package com.sarathi.missionactivitytask.ui.task

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sarathi.missionactivitytask.R

@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    icon: Painter,
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                width = 1.dp,
                color = if (isCompleted) Color.Green else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = title, style = MaterialTheme.typography.bodyLarge)
                    Text(text = subtitle, style = MaterialTheme.typography.bodyMedium)
                }
            }
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun TaskList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TaskCard(
            backgroundColor = Color(0xFFDFF6E5),
            icon = painterResource(id = R.drawable.ic_mission_inprogress),
            title = "Receipt of funds",
            subtitle = "5/5 tasks completed",
            isCompleted = true,
            onClick = {}
        )
        Spacer(modifier = Modifier.height(16.dp))
        Divider(
            color = Color.Gray,
            modifier = Modifier
                .width(1.dp)
                .height(40.dp)
                .align(Alignment.CenterHorizontally)
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.height(16.dp))
        TaskCard(
            backgroundColor = Color.White,
            icon = painterResource(id = R.drawable.ic_vo_name_icon),
            title = "Disbursement",
            subtitle = "02 Total Didis",
            isCompleted = false,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskListPreview() {
    TaskList()
}
