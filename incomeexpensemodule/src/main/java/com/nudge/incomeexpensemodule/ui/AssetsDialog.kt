package com.nudge.incomeexpensemodule.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nudge.core.model.uiModel.LivelihoodModel
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_8_dp
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
        title = {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Assets", style = MaterialTheme.typography.h6)
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
        },
        text = {
            Column {
                livelihoodModel.forEach { livelihood ->
                    Text(text = livelihood.name, style = defaultTextStyle)
                    Column() {
                        incomeExpenseSummaryUiModel?.assetsCountWithValue?.forEach { assetsCountWithValueItem ->
                            val assets =
                                incomeExpenseSummaryUiModel.livelihoodAssetMap[livelihood.livelihoodId]
                            val itemIndex = assets?.map { it.assetId }
                                ?.indexOf(assetsCountWithValueItem.assetId).value()
                            if (itemIndex != -1) {
                                AssetRow(
                                    assets?.get(itemIndex)?.name.value(),
                                    assetsCountWithValueItem.assetCount.toString(),
                                    "₹ ${assetsCountWithValueItem.totalAssetValue}"
                                )
                                Spacer(modifier = Modifier.height(dimen_8_dp))
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
        Text(text = label, style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = quantity,
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(text = amount, style = MaterialTheme.typography.body1)
    }
}

@Preview
@Composable
private fun AssetsDialogPreview() {
//    AssetsDialog({})
}
