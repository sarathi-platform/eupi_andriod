package com.nrlm.baselinesurvey.ui.common_components

import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.nrlm.baselinesurvey.R
import com.nrlm.baselinesurvey.ui.theme.searchFieldBg
import com.nrlm.baselinesurvey.ui.theme.textColorDark

@Composable
fun AlertDialogComponent(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    confirmButtonText: String = stringResource(R.string.default_confirm_button_text),
    dismissButtonText: String = stringResource(R.string.default_dismiss_button_text),
    icon: ImageVector? = null,
) {
    AlertDialog(
        containerColor = searchFieldBg,
        titleContentColor = textColorDark,
        textContentColor = textColorDark,
        properties = DialogProperties(securePolicy = SecureFlagPolicy.SecureOff),
        icon = {
            if (icon != null)
                Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(dismissButtonText)
            }
        }
    )
}