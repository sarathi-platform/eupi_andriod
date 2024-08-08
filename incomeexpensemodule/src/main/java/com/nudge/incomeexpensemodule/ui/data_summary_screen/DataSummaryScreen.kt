package com.nudge.incomeexpensemodule.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.incomeexpensemodule.R
import com.nudge.core.BLANK_STRING
import com.nudge.core.TabsCore
import com.nudge.core.enums.SubTabs
import com.nudge.core.enums.TabsEnum
import com.nudge.core.ui.commonUi.CustomSubTabLayout
import com.nudge.core.ui.commonUi.ToolBarWithMenuComponent
import com.nudge.core.ui.commonUi.componet_.component.ButtonPositive
import com.nudge.core.ui.events.DialogEvents
import com.nudge.core.ui.theme.assetValueIconColor
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.borderGreyLight
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.didiDetailItemStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_24_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.incomeCardBorderColor
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.quesOptionTextStyle
import com.nudge.incomeexpensemodule.events.DataSummaryScreenEvents
import com.nudge.incomeexpensemodule.navigation.navigateToAddEventScreen
import com.nudge.incomeexpensemodule.ui.component.SingleSelectDropDown
import com.nudge.incomeexpensemodule.ui.data_summary_screen.viewmodel.DataSummaryScreenViewModel
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.IncomeExpenseSummaryUiModel
import com.sarathi.dataloadingmangement.util.event.InitDataEvent
import java.util.UUID

@Composable
fun DataSummaryScreen(
    navController: NavHostController,
    viewModel: DataSummaryScreenViewModel,
    subjectId: Int,
    subjectName: String
) {
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(InitDataEvent.InitDataSummaryScreenState(subjectId = subjectId))
    }

    if (viewModel.showAssetDialog.value) {
        AssetsDialog(
            viewModel.incomeExpenseSummaryUiModel.value,
            viewModel.livelihoodModel,
            onDismissRequest = {
                viewModel.onEvent(DialogEvents.ShowDialogEvent(false))
            }
        )
    }

    ToolBarWithMenuComponent(
        title = subjectName,
        modifier = Modifier.fillMaxSize(),
        onBackIconClick = {
            navController.navigateUp()
        },
        onSearchValueChange = {},
        onBottomUI = { },
        onContentUI = { a, b, c ->
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                if (viewModel.livelihoodEvent.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AddEventButton(navController = navController, subjectId, subjectName)
                    }
                } else {
                    DataSummaryView(
                        viewModel,
                        navController,
                        subjectId,
                        subjectName,
                        viewModel.countMap
                    )
                }

            }
        },
        onSettingClick = { }) {

    }

}

@Composable
private fun DataSummaryView(
    viewModel: DataSummaryScreenViewModel,
    navController: NavHostController,
    subjectId: Int,
    subjectName: String,
    countMap: Map<SubTabs, Int>
) {
    TabBarContainer(countMap)
    Spacer(modifier = Modifier.height(16.dp))
    DropDownConatiner()
    Spacer(modifier = Modifier.height(16.dp))
    HeaderSection(viewModel.incomeExpenseSummaryUiModel.value!!) {

    }
    Spacer(modifier = Modifier.height(16.dp))
    EventsList(viewModel.livelihoodDropdownList.toList()) {
        viewModel.onEvent(DataSummaryScreenEvents.FilterDataForLivelihood(it))
    }

    Spacer(modifier = Modifier.height(16.dp))
    EventView()
    Spacer(modifier = Modifier.height(16.dp))
    ShowMoreButton(navController = navController, subjectId, subjectName)
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun TabBarContainer(countMap: Map<SubTabs, Int>) {
    val tabs = listOf<SubTabs>(SubTabs.LastWeekTab, SubTabs.LastMonthTab, SubTabs.Last3MonthsTab)
    TabsCore.setTabIndex(TabsEnum.DataSummaryTab.tabIndex)
    CustomSubTabLayout(
        parentTabIndex = TabsEnum.DataSummaryTab.tabIndex,
        tabs = tabs,
        countMap = countMap
    )
}

@Composable
fun DropDownConatiner() {
    SingleSelectDropDown(sources = listOf(ValuesDto(1, "item1"), ValuesDto(2, "item2"))) {

    }

}

@Composable
fun HeaderSection(
    incomeExpenseSummaryUiModel: IncomeExpenseSummaryUiModel,
    onAssetCountClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = incomeCardBorderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .background(Color(0xFFFFF3E0), shape = RoundedCornerShape(8.dp))
            .padding(dimen_10_dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "Income", style = getTextColor(newMediumTextStyle))
            Text(
                text = "₹ ${incomeExpenseSummaryUiModel.totalIncome}",
                style = getTextColor(defaultTextStyle)
            )
        }
        Column {
            Text(text = "Expense", style = getTextColor(newMediumTextStyle))
            Text(
                text = "₹ ${incomeExpenseSummaryUiModel.totalExpense}",
                style = getTextColor(defaultTextStyle)
            )
        }
        Column {
            Text(text = "Asset Value", style = getTextColor(newMediumTextStyle))
            Row(verticalAlignment = Alignment.CenterVertically) {
                incomeExpenseSummaryUiModel.assetsCountWithValue.forEach {
                    Icon(
                        painter = painterResource(id = R.drawable.goat_icon),
                        contentDescription = null,
                        tint = assetValueIconColor
                    )
                    Text(
                        text = it.assetCount.toString(),
                        style = getTextColor(didiDetailItemStyle),
                    )
                    Spacer(modifier = Modifier.width(dimen_5_dp))
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right_circle),
                    contentDescription = null,
                    tint = assetValueIconColor
                )
            }
        }
    }

}

@Composable
fun EventsList(livelihoodList: List<ValuesDto>, onValueSelected: (id: Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

        Text("Last 3 events:", style = getTextColor(defaultTextStyle))
        SingleSelectDropDown(sources = livelihoodList) {
            onValueSelected(it)
        }
    }
}


@Composable
fun ShowMoreButton(navController: NavHostController, subjectId: Int, subjectName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
            }, horizontalArrangement = Arrangement.Center
    ) {
        TextButton(
            onClick = {
                navigateToAddEventScreen(
                    navController = navController,
                    subjectName = subjectName,
                    subjectId = subjectId,
                    transactionID = UUID.randomUUID().toString()
                )
            },
            modifier = Modifier
                .height(48.dp)
                .border(
                    width = 1.dp,
                    color = borderGreyLight,
                    shape = RoundedCornerShape(8.dp)
                ),
            shape = RoundedCornerShape(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Show more",
                    textAlign = TextAlign.Center,
                    style = getTextColor(defaultTextStyle),
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = assetValueIconColor
                )
            }
        }
    }

}


@Composable
private fun EventView() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = dimen_10_dp)
    ) {
        EventHeader()
        EventDetails()
    }
}

@Composable
private fun EventHeader() {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            TextWithPaddingEnd(
                text = "Event:",
                style = getTextColor(quesOptionTextStyle)
            )
            Text(
                text = "Asset Purchase",
                style = getTextColor(newMediumTextStyle)
            )
        }
        Text(
            text = "15 Jan’ 24",
            style = getTextColor(quesOptionTextStyle)
        )

    }
}

@Composable
private fun EventDetails() {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            TextWithPaddingEnd(
                text = "Amount:",
                style = getTextColor(quesOptionTextStyle)
            )
            Text(
                text = "- ₹ 8000",
                style = getTextColor(newMediumTextStyle)
            )
        }
        Row {
            TextWithPaddingEnd(
                text = "Assets:",
                style = getTextColor(quesOptionTextStyle)
            )
            Text(
                text = "+2",
                style = getTextColor(newMediumTextStyle)
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "ArrowForward Icon",
            modifier = Modifier.size(dimen_24_dp),
            tint = blueDark
        )

    }
}

@Composable
private fun TextWithPaddingEnd(text: String, style: TextStyle) {
    Text(
        modifier = Modifier.padding(end = dimen_5_dp),
        text = text,
        style = style
    )
}

@Composable
private fun AddEventButton(navController: NavHostController, subjectId: Int, subjectName: String) {
    ButtonPositive(buttonTitle = "Add Event", isActive = true, isArrowRequired = true) {
        navigateToAddEventScreen(
            navController = navController,
            subjectName = subjectName,
            subjectId = subjectId,
            transactionID = BLANK_STRING
        )
    }
}

private fun getTextColor(textColor: TextStyle, color: Color = blueDark): TextStyle =
    textColor.copy(color)


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val countMap: MutableMap<SubTabs, Int> = mutableMapOf()
    countMap.put(SubTabs.All, 1)
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
//        DataSummaryView(navController = rememberNavController(), 0, "", countMap = countMap)
    }
}