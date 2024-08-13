package com.nudge.incomeexpensemodule.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.redOffline
import com.sarathi.dataloadingmangement.enums.EntryFlowTypeEnum
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.SubjectLivelihoodEventSummaryUiModel

fun getAssetCountForEvent(item: SubjectLivelihoodEventSummaryUiModel): String {
    return if (item.assetJournalFlow?.toLowerCase()
            ?.equals(EntryFlowTypeEnum.OutFlow.name.toLowerCase()) == true
    ) {
        "- ${item.assetCount}"
    } else {
        "+ ${item.assetCount}"
    }
}

fun getAmountColorForEvent(item: SubjectLivelihoodEventSummaryUiModel): TextStyle {
    return if (item.moneyJournalFlow?.toLowerCase()
            ?.equals(EntryFlowTypeEnum.OutFlow.name.toLowerCase()) == true
    ) {
        newMediumTextStyle.copy(color = redOffline)
    } else
        newMediumTextStyle.copy(color = greenOnline)

}

fun getAmountForEvent(item: SubjectLivelihoodEventSummaryUiModel): String {
    return if (item.moneyJournalFlow?.toLowerCase()
            ?.equals(EntryFlowTypeEnum.OutFlow.name.toLowerCase()) == true
    ) {
        "- ₹ ${item.transactionAmount}"
    } else {
        "+ ₹ ${item.transactionAmount}"
    }
}

fun getTextColor(textColor: TextStyle, color: Color = blueDark): TextStyle =
    textColor.copy(color)

fun List<ValuesDto>.findById(id: Int): ValuesDto? {
    val index = this.map { it.id }.indexOf(id)

    if (index == -1)
        return null

    return this[index]

}