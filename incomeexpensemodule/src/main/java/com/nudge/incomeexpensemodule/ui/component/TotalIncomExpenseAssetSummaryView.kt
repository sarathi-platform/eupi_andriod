package com.nudge.incomeexpensemodule.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.incomeexpensemodule.R
import com.nudge.core.ui.commonUi.CustomHorizontalSpacer
import com.nudge.core.ui.theme.assetValueIconColor
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.didiDetailItemStyle
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.value
import com.nudge.incomeexpensemodule.utils.getTextColor
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.IncomeExpenseSummaryUiModel

@Composable
fun TotalIncomeExpenseAssetSummaryView(
    incomeExpenseSummaryUiModel: IncomeExpenseSummaryUiModel?,
    onAssetCountClicked: () -> Unit
) {
    Column {
        Text(text = "Income", style = getTextColor(newMediumTextStyle))
        Text(
            text = "₹ ${incomeExpenseSummaryUiModel?.totalIncome.value()}",
            style = getTextColor(defaultTextStyle)
        )
    }
    Column {
        Text(text = "Expense", style = getTextColor(newMediumTextStyle))
        Text(
            text = "₹ ${incomeExpenseSummaryUiModel?.totalExpense.value()}",
            style = getTextColor(defaultTextStyle)
        )
    }
    Column(
        modifier = Modifier
            .clickable {
                onAssetCountClicked()
            }
    ) {
        Text(text = "Asset Count", style = getTextColor(newMediumTextStyle))
        Row(verticalAlignment = Alignment.CenterVertically) {
            incomeExpenseSummaryUiModel?.totalAssetCountForLivelihood?.forEach {
                if (it.value != 0) {
                    Icon(
                        painter = painterResource(id = R.drawable.goat_icon),
                        contentDescription = null,
                        tint = assetValueIconColor
                    )
                    CustomHorizontalSpacer(size = dimen_5_dp)
                    Text(
                        text = it.value.toString(),
                        style = getTextColor(didiDetailItemStyle),
                    )
                    Spacer(modifier = Modifier.width(dimen_5_dp))
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right_circle),
                contentDescription = null,
                tint = assetValueIconColor
            )
        }
    }
}