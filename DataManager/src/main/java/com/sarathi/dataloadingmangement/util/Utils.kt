import android.text.TextUtils
import androidx.compose.ui.graphics.Color
import com.nudge.core.ui.theme.greenLight
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.greyBorder
import com.nudge.core.ui.theme.sectionIconCompletedBg
import com.nudge.core.ui.theme.sectionIconNotStartedBg
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.data.entities.livelihood.SubjectLivelihoodMappingEntity
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.SubjectEntityWithLivelihoodMappingUiModel
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum

fun SubjectEntityWithLivelihoodMappingUiModel.getSubjectLivelihoodMappingEntity(userId: String): SubjectLivelihoodMappingEntity {
    return SubjectLivelihoodMappingEntity(
        userId = userId,
        subjectId = this.subjectId,
        primaryLivelihoodId = this.primaryLivelihoodId,
        secondaryLivelihoodId = this.secondaryLivelihoodId
    )
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

enum class ComponentName() {

    SECTION_BOX_CONTAINER_COLOR,
    SECTION_BOX_BORDER_COLOR,
    SECTION_BOX_ICON_CONTAINER_COLOR,
    SECTION_BOX_TEXT_COLOR;

}