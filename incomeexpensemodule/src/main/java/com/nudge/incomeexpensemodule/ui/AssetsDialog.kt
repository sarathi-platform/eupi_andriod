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
import com.nudge.core.ui.theme.dimen_8_dp

@Composable
fun AssetsDialog(
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
                AssetRow("Adult Male:", "1", "₹ 2000")
                Spacer(modifier = Modifier.height(dimen_8_dp))
                AssetRow("Adult Female:", "5", "₹ 6000")
            }
        },
        buttons = {
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
    AssetsDialog({})
}
