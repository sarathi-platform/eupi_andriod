package com.sarathi.surveymanager.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_100_dp
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_30_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.greyBorderColor
import com.nudge.core.ui.theme.greyColor
import com.nudge.core.ui.theme.languageItemActiveBg
import com.nudge.core.ui.theme.white
import com.sarathi.surveymanager.R

@SuppressLint("UnrememberedMutableState")
@Composable
fun CollapsibleCard(
    title: String = BLANK_STRING,
    summaryCount: Int = 0,
    onContentUI: @Composable () -> Unit,
    isEditable: Boolean = true,
    onClick: () -> Unit
) {
    val expanded = remember(summaryCount) {
        mutableStateOf(summaryCount > 0)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimen_16_dp)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = dimen_30_dp
            ), modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        ) {
            Column(modifier = Modifier
                .clip(RoundedCornerShape(dimen_6_dp))
                .border(
                    width = dimen_1_dp, color = greyBorderColor, shape = RoundedCornerShape(
                        dimen_6_dp
                    )
                )
                .fillMaxWidth()
                .background(white)
                .clickable { expanded.value = !expanded.value }) {
                Row(modifier = Modifier
                    .clickable(enabled = isEditable) {
                        onClick()
                    }
                    .fillMaxWidth()
                    .background(if (isEditable) blueDark else languageItemActiveBg)
                    .padding(dimen_10_dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Text(
                        text = title,
                        style = defaultTextStyle.copy(color = if (isEditable) white else greyColor)
                    )
                }
                if (summaryCount > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(white),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(dimen_10_dp)
                        ) {
                            Text(
                                text = stringResource(R.string.summary),
                                style = defaultTextStyle.copy(color = blueDark),
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = blueDark,
                            modifier = Modifier
                                .size(50.dp)
                                .padding(dimen_10_dp)
                                .rotate(if (expanded.value) 180f else 0f)
                        )
                    }
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimen_10_dp)
        )
        AnimatedVisibility(
            visible = expanded.value, enter = expandVertically(), exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier.background(white)
            ) {
                onContentUI()
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CollapsibleCard(onClick = {}, onContentUI = {})
}