package com.nudge.incomeexpensemodule.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.incomeexpensemodule.R
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.commonUi.CircularImageViewComponent
import com.nudge.core.ui.theme.assetValueIconColor
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.buttonTextStyle
import com.nudge.core.ui.theme.didiDetailItemStyle
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_24_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.dimen_4_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.incomeCardTopViewColor
import com.nudge.core.ui.theme.smallTextStyleWithNormalWeight
import com.nudge.core.ui.theme.smallerTextStyle
import com.nudge.core.ui.theme.white

@Composable
fun UserProfileCard(
    name: String,
    address: String,
    location: String,
    lastUpdated: String,
    income: String,
    expense: String,
    assetValue: String,
//    imageRes: Int
) {
    BasicCardView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimen_8_dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(incomeCardTopViewColor),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Text(
                    text = "Last updated: $lastUpdated",
                    style = getTextColor(textColor = smallerTextStyle),
                    modifier = Modifier.padding(vertical = dimen_2_dp, horizontal = dimen_5_dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimen_8_dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    CircularImageViewComponent(modifier = Modifier, Uri.EMPTY)
                    Spacer(modifier = Modifier.width(dimen_8_dp))
                    Column {
                        Text(text = name, style = getTextColor(buttonTextStyle))
                        Text(text = address, style = getTextColor(smallTextStyleWithNormalWeight))
                    }
                }

                Row {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "ArrowForward Icon",
                        modifier = Modifier.size(dimen_24_dp),
                        tint = blueDark
                    )
                }
            }

            Row(
                modifier = Modifier.padding(start = dimen_8_dp, bottom = dimen_8_dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    modifier = Modifier.size(dimen_16_dp),
                    tint = blueDark
                )
                Text(text = location, style = getTextColor(smallTextStyleWithNormalWeight))
            }
            Divider()

            Column(modifier = Modifier.padding(dimen_8_dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Calendar Icon",
                        modifier = Modifier.size(dimen_16_dp),
                        tint = blueDark
                    )
                    Spacer(modifier = Modifier.width(dimen_4_dp))
                    Text(
                        text = "Last 1 Month",
                        style = getTextColor(smallTextStyleWithNormalWeight)
                    )
                }
                Spacer(modifier = Modifier.height(dimen_8_dp))
                IncomeExpenseAssetAmountView(income, expense, assetValue)
            }
        }
    }
}

@Composable
fun IncomeExpenseAssetAmountView(income: String, expense: String, assetValue: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .padding(horizontal = dimen_8_dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Income", style = getTextColor(smallTextStyleWithNormalWeight))
            Text(text = income, style = getTextColor(didiDetailItemStyle))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Expense", style = getTextColor(smallTextStyleWithNormalWeight))
            Text(text = expense, style = getTextColor(didiDetailItemStyle))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Asset Value",
                style = getTextColor(smallTextStyleWithNormalWeight)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = assetValue,
                    style = getTextColor(didiDetailItemStyle),
                )
                Spacer(modifier = Modifier.width(dimen_5_dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right_circle),
                    contentDescription = null,
                    tint = assetValueIconColor
                )
            }
        }
    }
}

private fun getTextColor(textColor: TextStyle, color: Color = blueDark): TextStyle =
    textColor.copy(color)

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun UserProfileCardList() {
    Column {
        UserProfileCard(
            name = "Shanti Devi",
            address = "#45, Killu dada",
            location = "Sundar Pahari",
            lastUpdated = "10 days ago",
            income = "₹ 2000",
            expense = "₹ 500",
            assetValue = "₹ 8000",
//            imageRes = R.drawable.profile_img
        )
        UserProfileCard(
            name = "Surbhi Verma",
            address = "#45, Killu dada",
            location = "Sundar Pahari",
            lastUpdated = "10 days ago",
            income = "₹ 1000",
            expense = "₹ 500",
            assetValue = "₹ 1000",
//            imageRes = R.drawable.profile_img
        )
    }
}
