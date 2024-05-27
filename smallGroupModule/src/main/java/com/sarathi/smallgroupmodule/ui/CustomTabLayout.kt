package com.sarathi.smallgroupmodule.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sarathi.smallgroupmodule.ui.theme.blueDark
import com.sarathi.smallgroupmodule.ui.theme.borderGrey
import com.sarathi.smallgroupmodule.ui.theme.dimen_1_dp
import com.sarathi.smallgroupmodule.ui.theme.dimen_6_dp
import com.sarathi.smallgroupmodule.ui.theme.tabBgColor
import com.sarathi.smallgroupmodule.ui.theme.white

@Composable
fun CustomTabLayout(
    modifier: Modifier = Modifier
) {

    var tabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("Didi", "Small Group")

    TabRow(selectedTabIndex = tabIndex) {

        tabs.forEachIndexed { index, s ->


        }

    }


}


@Composable
fun TabItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    onClick: () -> Unit,
    tabWidth: Dp,
    text: String,
) {
    val tabBgColor: Color = if (isSelected) tabBgColor else white
    val borderColor: Color = if (isSelected) tabBgColor else borderGrey
    Box(
        modifier = Modifier.border(
            dimen_1_dp, borderColor, RoundedCornerShape(dimen_6_dp)
        )
    ) {


        Text(
            modifier = Modifier
                .clickable {
                    onClick()
                }
                .width(tabWidth)
                .padding(
                    vertical = 8.dp,
                    horizontal = 12.dp,
                ),
            text = text,
            color = blueDark,
            textAlign = TextAlign.Center,
        )
    }
}