package com.nudge.incomeexpensemodule.ui.component

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.incomeexpensemodule.R
import com.nudge.core.formatToIndianRupee
import com.nudge.core.getFileNameFromURL
import com.nudge.core.ui.commonUi.CustomHorizontalSpacer
import com.nudge.core.ui.theme.assetValueIconColor
import com.nudge.core.ui.theme.defaultTextStyle
import com.nudge.core.ui.theme.didiDetailItemStyle
import com.nudge.core.ui.theme.dimen_0_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.utils.FileUtils
import com.nudge.core.value
import com.nudge.incomeexpensemodule.utils.getTextColor
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.IncomeExpenseSummaryUiModel
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.SubjectEntityWithLivelihoodMappingUiModel
import sortTotalAssetCountForLivelihood
import java.math.BigDecimal

@Composable
fun TotalIncomeExpenseAssetSummaryView(
    incomeExpenseSummaryUiModel: IncomeExpenseSummaryUiModel?,
    subjectLivelihoodMapping: List<SubjectEntityWithLivelihoodMappingUiModel>,
    onAssetCountClicked: () -> Unit
) {
    val context = LocalContext.current
    Column {
        Text(text = stringResource(R.string.income), style = getTextColor(newMediumTextStyle))
        Text(
            text = formatToIndianRupee(BigDecimal(incomeExpenseSummaryUiModel?.totalIncome.value()).toPlainString()),
            style = getTextColor(defaultTextStyle)
        )
    }
    Column {
        Text(text = stringResource(R.string.expense), style = getTextColor(newMediumTextStyle))
        Text(
            text = formatToIndianRupee(BigDecimal(incomeExpenseSummaryUiModel?.totalExpense.value()).toPlainString()),
            style = getTextColor(defaultTextStyle)
        )
    }
    Column(
        modifier = Modifier
            .clickable {
                if (incomeExpenseSummaryUiModel?.totalAssetCountForLivelihood?.filter { it.value != 0 }
                        ?.isNotEmpty() == true) {
                    onAssetCountClicked()
                }
            }
    ) {
        Text(text = stringResource(R.string.total_asset), style = getTextColor(newMediumTextStyle))
        if (incomeExpenseSummaryUiModel?.totalAssetCountForLivelihood?.isNotEmpty() == true) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                sortTotalAssetCountForLivelihood(
                    incomeExpenseSummaryUiModel,
                    subjectLivelihoodMapping
                )?.forEach {
                    incomeExpenseSummaryUiModel.imageUriForLivelihood.get(it.key)
                        ?.let { fileName ->
                            val fileNameFromUrl = getFileNameFromURL(fileName)
                            FileUtils.getImageUri(context = context, fileName = fileNameFromUrl)
                                ?.let { it1 -> ImageViewer(uri = it1) }
                        }
                    CustomHorizontalSpacer(size = dimen_5_dp)
                    Text(
                        text = it.value.toString(),
                        style = getTextColor(didiDetailItemStyle),
                    )
                    Spacer(modifier = Modifier.width(dimen_5_dp))
                }
                if (incomeExpenseSummaryUiModel.totalAssetCountForLivelihood.filter { it.value != 0 }
                        .isNotEmpty()) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right_circle),
                        contentDescription = null,
                        tint = assetValueIconColor
                    )
                }
            }
        }
    }
}

@Composable
fun ImageViewer(uri: Uri) {
    AsyncImage(
        model = uri,
        contentDescription = "Loaded Image",
        modifier = Modifier
            .size(15.dp)
            .padding(vertical = dimen_0_dp)
    )

}