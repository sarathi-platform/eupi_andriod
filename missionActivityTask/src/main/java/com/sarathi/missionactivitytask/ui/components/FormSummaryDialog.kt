package com.sarathi.missionactivitytask.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nudge.core.BLANK_STRING
import com.nudge.core.formatToIndianRupee
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.smallTextStyle
import com.nudge.core.ui.theme.white
import com.sarathi.contentmodule.ui.component.ButtonPositive
import com.sarathi.dataloadingmangement.model.uiModel.DisbursementFormSummaryUiModel
import com.sarathi.dataloadingmangement.ui.component.ButtonNegative
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.ui.disbursement_summary_screen.TextRow

@Composable
fun FormSummaryDialog(
    imageUri: Uri?,
    disbursementFormSummaryUiModel: DisbursementFormSummaryUiModel,
    positiveButtonTitle: String? = BLANK_STRING,
    negativeButtonTitle: String? = BLANK_STRING,
    dismissOnBackPress: Boolean? = true,
    onPositiveButtonClick: () -> Unit,
    onNegativeButtonClick: () -> Unit
) {
    Dialog(
        onDismissRequest = { }, properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = dismissOnBackPress ?: true
        )
    ) {
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .background(color = white, shape = RoundedCornerShape(6.dp)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimen_16_dp, vertical = dimen_16_dp),
                        horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularImageViewComponent(modifier = Modifier, imageUri ?: Uri.EMPTY) {}
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = disbursementFormSummaryUiModel.subjectName,
                                style = defaultTextStyle.copy(blueDark),
                            )
                            Text(
                                text = disbursementFormSummaryUiModel.villageName,
                                style = smallTextStyle.copy(blueDark),
                            )
                        }

                    }
                    Column(modifier = Modifier.padding(start = dimen_16_dp, end = dimen_16_dp)) {
                        TextRow(
                            text1 = stringResource(R.string.mode),
                            text2 = disbursementFormSummaryUiModel.mode
                        )
                        TextRow(
                            text1 = stringResource(R.string.nature),
                            text2 = disbursementFormSummaryUiModel.nature,
                        )
                        TextRow(
                            text1 = stringResource(R.string.amount),
                            text2 = formatToIndianRupee(disbursementFormSummaryUiModel.amount)
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {

                            if (!negativeButtonTitle.isNullOrEmpty()) {
                                ButtonNegative(
                                    buttonTitle = negativeButtonTitle,
                                    isArrowRequired = false,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    onNegativeButtonClick()
                                }

                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }

                            Spacer(modifier = Modifier.width(8.dp))
                            positiveButtonTitle?.let {
                                if (!it.isNullOrEmpty()) {
                                    ButtonPositive(
                                        buttonTitle = it,
                                        isArrowRequired = false,
                                        isActive = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(vertical = 2.dp)
                                    ) {
                                        onPositiveButtonClick()
                                    }
                                }
                            }

                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

