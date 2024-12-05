package com.nudge.core.ui.commonUi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.R
import com.nudge.core.TabsCore
import com.nudge.core.enums.SubTabs
import com.nudge.core.ui.theme.borderGrey
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.mediumTextStyle
import com.nudge.core.ui.theme.tabBgColor
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white

@Composable
fun CustomSubTabLayout(
    parentTabIndex: Int,
    tabs: List<SubTabs>,
    countMap: Map<SubTabs, Int> = mapOf(),
    enableClickOnTab: (() -> Boolean) = { true }
) {

    val state = rememberLazyListState()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
        modifier = Modifier.fillMaxWidth(),
        state = state
    ) {

        itemsIndexed(tabs) { index, tab ->
            TabItem(
                isSelected = TabsCore.getSubTabForTabIndex(parentTabIndex) == index,
                onClick = {
                    if (enableClickOnTab.invoke()) {
                        TabsCore.setSubTabIndex(parentTabIndex, index)
//                    TabsCore.getSubTabIndex().value = tab.id
                    }
                },
                text = getTabTitle(countMap, tab)
            )
        }

    }
}

@Composable
fun CustomSubTabLayoutWithCallBack(
    parentTabIndex: Int,
    tabs: List<SubTabs>,
    countMap: Map<SubTabs, Int> = mapOf(),
    onClick: () -> Unit
) {

    val state = rememberLazyListState()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
        modifier = Modifier.fillMaxWidth(),
        state = state
    ) {

        itemsIndexed(tabs) { index, tab ->
            TabItem(
                isSelected = TabsCore.getSubTabForTabIndex(parentTabIndex) == index,
                onClick = {
                    TabsCore.setSubTabIndex(parentTabIndex, index)
                    onClick()
                },
                text = getTabTitle(countMap, tab)
            )
        }

    }
}

@Composable
fun TabItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    onClick: () -> Unit,
    text: String,
) {
    val tabBgColor: Color = if (isSelected) tabBgColor else white
    val borderColor: Color = if (isSelected) tabBgColor else borderGrey

    val interactionSource = remember { MutableInteractionSource() }
    val ripple =
        rememberRipple(bounded = true, color = tabBgColor)

    Box(
        modifier = Modifier
            .border(
                width = dimen_1_dp,
                color = borderColor,
                shape = RoundedCornerShape(dimen_6_dp)
            )
            .background(
                color = tabBgColor,
                shape = RoundedCornerShape(dimen_6_dp)
            )
            .selectable(
                selected = isSelected,
                onClick = onClick,
                enabled = true,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = ripple
            ),
        contentAlignment = Alignment.Center
    ) {


        Text(
            modifier = Modifier
                .clickable {
                    onClick()
                }
                .padding(
                    vertical = 8.dp,
                    horizontal = 12.dp,
                ),
            text = text,
            color = textColorDark,
            style = mediumTextStyle,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

@Composable
private fun getTabTitle(
    countMap: Map<SubTabs, Int>,
    tab: SubTabs
): String {
    var tabTitle = getTabName(tab = tab)
    if (countMap.isNotEmpty()) {
        val count = countMap[tab]
        count?.let {
            tabTitle = "$tabTitle ($it)"
        }
    }
    return tabTitle
}

@Composable
fun getTabName(tab: SubTabs): String {

    return when (tab) {
        SubTabs.DidiTab -> stringResource(R.string.didi_sub_tab_title)
        SubTabs.SmallGroupTab -> stringResource(R.string.small_group_sub_tab_title)
        SubTabs.All -> stringResource(R.string.all)
        SubTabs.NoEntryMonthTab -> stringResource(R.string.no_entry_this_month)
        SubTabs.NoEntryWeekTab -> stringResource(R.string.no_entry_this_week)
        SubTabs.LastWeekTab -> stringResource(R.string.last_week)
        SubTabs.LastMonthTab -> stringResource(R.string.last_month)
        SubTabs.Last3MonthsTab -> stringResource(R.string.last_3_months)
        SubTabs.CustomDateRange -> stringResource(R.string.custom_date)
        SubTabs.Step1 -> stringResource(R.string.reopen_activity_step_1)
        SubTabs.Step2 -> stringResource(R.string.reopen_activity_step_2)
        else -> {
            BLANK_STRING
        }
    }
}