package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.DATE_TAG
import com.sarathi.dataloadingmangement.DISBURSED_AMOUNT_TAG
import com.sarathi.dataloadingmangement.DISBURSEMENT_DATE_TAG
import com.sarathi.dataloadingmangement.MODE_TAG
import com.sarathi.dataloadingmangement.NATURE_TAG
import com.sarathi.dataloadingmangement.NO_OF_POOR_DIDI_TAG
import com.sarathi.dataloadingmangement.RECEIVED_AMOUNT_TAG
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.repository.IMoneyJournalRepository
import com.sarathi.dataloadingmangement.util.constants.QuestionType

class SaveTransactionMoneyJournalUseCase(private val repository: IMoneyJournalRepository) {


    suspend fun saveMoneyJournalForGrant(
        subjectId: Int,
        subjectType: String,
        referenceId: String,
        grantId: Int,
        grantType: String,
        questionUiModels: List<QuestionUiModel>
    ) {
        val amountInString = questionUiModels.find {
            it.tagId.contains(DISBURSED_AMOUNT_TAG) || it.tagId.contains(RECEIVED_AMOUNT_TAG)
        }?.options?.firstOrNull()?.selectedValue
        val date = questionUiModels.find {
            it.tagId.contains(DATE_TAG) || it.tagId.contains(DISBURSEMENT_DATE_TAG)
        }?.options?.firstOrNull()?.selectedValue
        var particulars = ""
        var option = ""
        questionUiModels.filter {
            it.tagId.contains(MODE_TAG) || it.tagId.contains(NATURE_TAG) || it.tagId.contains(
                NO_OF_POOR_DIDI_TAG
            )
        }.forEach { questionUiModel ->
            option = option + questionUiModel.questionSummary + "="
            if (questionUiModel.type == QuestionType.MultiSelectDropDown.name || questionUiModel.type == QuestionType.SingleSelectDropDown.name) {
                questionUiModel.options?.filter { it.isSelected == true }
                    ?.forEachIndexed { index, it ->
                        option += "${it.originalValue}"
                        if (index != questionUiModel.options?.size?.minus(1)) {
                            option += ","
                        }
                    }
            } else {
                questionUiModel.options?.forEach {
                    option += " ${it.selectedValue} "
                }
            }
            option = "$option|"
            particulars = option
        }
        particulars += subjectType



        repository.saveAndUpdateMoneyJournalTransaction(
            amountInString?.toInt() ?: 0,
            date ?: BLANK_STRING,
            particulars,
            referenceId,
            grantId,
            grantType,
            subjectType,
            subjectId
        )

    }

    suspend fun deleteTransactionFromMoneyJournal(transactionID: String, subjectId: Int) {
        repository.deleteMoneyJournalTransaction(transactionID, subjectId)
    }
}




