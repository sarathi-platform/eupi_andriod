package com.nudge.incomeexpensemodule.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.incomeexpensemodule.R
import com.nudge.core.model.uiModel.LivelihoodModel
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.mediumTextStyle
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.textColorGreyLight
import com.nudge.core.value
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.IncomeExpenseSummaryUiModel

@Composable
fun AssetsDialog(
    incomeExpenseSummaryUiModel: IncomeExpenseSummaryUiModel?,
    livelihoodModel: List<LivelihoodModel>,
    onDismissRequest: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(roundedCornerRadiusDefault),
        title = {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.assets),
                    style = mediumTextStyle,
                    color = textColorDark
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                    )
                }
            }
        },
        text = {
            Column {
                livelihoodModel.forEach { livelihood ->
                    if (incomeExpenseSummaryUiModel?.totalAssetCountForLivelihood?.containsKey(
                            livelihood.programLivelihoodId
                        ) == true
                        && incomeExpenseSummaryUiModel.totalAssetCountForLivelihood[livelihood.programLivelihoodId] != 0
                    ) {
                        Text(
                            text = livelihood.name,
                            style = defaultTextStyle.copy(fontWeight = FontWeight.Bold),
                            color = textColorDark
                        )
                        CustomVerticalSpacer(size = dimen_5_dp)
                        Column() {
                            incomeExpenseSummaryUiModel?.assetsCountWithValue?.distinctBy {
                                Pair(
                                    it.livelihoodId,
                                    it.assetId
                                )
                            }
                                ?.forEach { assetsCountWithValueItem ->
                                    val assets =
                                        incomeExpenseSummaryUiModel.livelihoodAssetMap[livelihood.programLivelihoodId]
                                    val itemIndex =
                                        assets?.map { Pair(it.livelihoodId, it.assetId) }
                                            ?.indexOf(
                                                Pair(
                                                    assetsCountWithValueItem.livelihoodId,
                                                    assetsCountWithValueItem.assetId
                                                )
                                            ).value()
                                    if (itemIndex != -1) {
                                        AssetRow(
                                            assets?.get(itemIndex)?.name.value(),
                                            assetsCountWithValueItem.assetCount.toString(),
                                            "₹ ${assetsCountWithValueItem.totalAssetValue}"
                                        )
                                    Spacer(modifier = Modifier.height(dimen_6_dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        buttons = {
            /**
             * Implementation not required. **/
        },
    )

}

@Composable
fun AssetRow(label: String, quantity: String, amount: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$label: ",
            style = defaultTextStyle.copy(fontWeight = FontWeight.SemiBold),
            color = textColorGreyLight
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = quantity,
            style = defaultTextStyle.copy(fontWeight = FontWeight.Bold), color = textColorDark
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = amount,
            style = defaultTextStyle.copy(fontWeight = FontWeight.Bold),
            color = textColorDark
        )
    }
}

@Preview
@Composable
private fun AssetsDialogPreview() {
//    AssetsDialog({})
}
