import android.text.TextUtils
import androidx.compose.ui.graphics.Color
import com.nudge.core.ui.theme.greenLight
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.greyBorder
import com.nudge.core.ui.theme.sectionIconCompletedBg
import com.nudge.core.ui.theme.sectionIconNotStartedBg
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.MONEY_JOURNAL_AMOUNT_TAG
import com.sarathi.dataloadingmangement.MONEY_JOURNAL_DATE_TAG
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.model.uiModel.QuestionUiModel
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.SubjectEntityWithLivelihoodMappingUiModel
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum

fun List<SubjectEntityWithLivelihoodMappingUiModel>.getSubjectLivelihoodMappingEntity(userId: String): List<SubjectLivelihoodMappingEntity> {
    val list = ArrayList<SubjectLivelihoodMappingEntity>()
    this.forEach {
        list.add(
            SubjectLivelihoodMappingEntity(
                userId = userId,
                subjectId = it.subjectId,
                livelihoodId = it.livelihoodId,
                status = 1,
                type = 1
            )
        )
    }
    return list
}

fun getColorForComponent(status: String, componentName: ComponentName): Color {

    return when (componentName) {
        ComponentName.SECTION_BOX_BORDER_COLOR -> {
            if (TextUtils.equals(
                    status.toLowerCase(),
                    SurveyStatusEnum.COMPLETED.name.toLowerCase()
                )
            ) greenOnline else greyBorder
        }

        ComponentName.SECTION_BOX_CONTAINER_COLOR -> {
            if (TextUtils.equals(
                    status.toLowerCase(),
                    SurveyStatusEnum.COMPLETED.name.toLowerCase()
                )
            ) greenLight else white
        }

        ComponentName.SECTION_BOX_ICON_CONTAINER_COLOR -> {
            if (TextUtils.equals(
                    status.toLowerCase(),
                    SurveyStatusEnum.COMPLETED.name.toLowerCase()
                )
            ) sectionIconCompletedBg else sectionIconNotStartedBg
        }

        ComponentName.SECTION_BOX_TEXT_COLOR -> {
            if (TextUtils.equals(
                    status.toLowerCase(),
                    SurveyStatusEnum.COMPLETED.name.toLowerCase()
                )
            ) greenOnline else textColorDark
        }

    }

}

fun getMoneyJournalEntryData(
    questionUiModels: List<QuestionUiModel>,
    subjectType: String
): Triple<String?, String?, String> {
    val amountInString = questionUiModels.find {
        it.tagId.contains(MONEY_JOURNAL_AMOUNT_TAG)
    }?.options?.firstOrNull()?.selectedValue
    val date = questionUiModels.find {
        it.tagId.contains(MONEY_JOURNAL_DATE_TAG)
    }?.options?.firstOrNull()?.selectedValue
    var particulars = ""
    var option = ""
    questionUiModels.forEach { questionUiModel ->
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
    return Triple(amountInString, date, particulars)
}

enum class ComponentName() {

    SECTION_BOX_CONTAINER_COLOR,
    SECTION_BOX_BORDER_COLOR,
    SECTION_BOX_ICON_CONTAINER_COLOR,
    SECTION_BOX_TEXT_COLOR;

}