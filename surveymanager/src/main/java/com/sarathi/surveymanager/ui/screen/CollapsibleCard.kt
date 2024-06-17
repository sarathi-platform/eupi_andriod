package com.sarathi.surveymanager.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nudge.core.ui.theme.blue
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_30_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.greyBorderColor
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.BLANK_STRING

@Composable
fun CollapsibleCard(
    title: String = BLANK_STRING,
    summaryCount: Int = 0,
    onContentUI: @Composable () -> Unit,
    isEditable: Boolean = true,
    onClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    androidx.compose.material3.Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimen_30_dp
        ), modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(dimen_6_dp))
            .border(
                width = dimen_1_dp, color = greyBorderColor, shape = RoundedCornerShape(dimen_6_dp)
            )
            .background(Color.Transparent)
    ) {

        Column(modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .background(white)
            .clickable { expanded = !expanded } // Toggle expanded state on click
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen_10_dp)
                    .clickable(enabled = isEditable) {
                        onClick()
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(
                    text = title, style = defaultTextStyle.copy(color = blue)
                )
            }
            if (summaryCount > 0) {
                Divider(
                    color = greyBorderColor,
                    thickness = 0.5.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimen_10_dp)
                )
                Row {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(dimen_10_dp)
                    ) {
                        Text(
                            text = "Summary ",
                            style = defaultTextStyle,
                        )
                        Text(
                            text = "$summaryCount",
                            style = defaultTextStyle
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier
                            .padding(dimen_10_dp)
                            .rotate(if (expanded) 180f else 0f)
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded, enter = expandVertically(), exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = dimen_16_dp)
                ) {
                    onContentUI()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CollapsibleCard(onClick = {}, onContentUI = {})
}