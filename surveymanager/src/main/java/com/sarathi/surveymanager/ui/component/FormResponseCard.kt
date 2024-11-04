package com.sarathi.surveymanager.ui.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.showCustomToast
import com.nudge.core.ui.commonUi.CircularImageViewComponent
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.borderGreyLight
import com.nudge.core.ui.theme.defaultCardElevation
import com.nudge.core.ui.theme.dimen_14_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.white
import com.nudge.core.value
import com.sarathi.dataloadingmangement.model.uiModel.SurveyAnswerFormSummaryUiModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyCardModel
import com.sarathi.dataloadingmangement.model.uiModel.SurveyConfigCardSlots
import com.sarathi.dataloadingmangement.model.uiModel.SurveyConfigCardSlots.Companion.CONFIG_SLOT_TYPE_TAG
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import com.sarathi.surveymanager.R
import com.sarathi.surveymanager.constants.PIPE_DELIMITER

@Composable
fun FormResponseCard(
    modifier: Modifier = Modifier,
    referenceId: String,
    surveyAnswerFormSummaryUiModelList: List<SurveyAnswerFormSummaryUiModel>,
    surveyConfig: Map<String, SurveyCardModel>,
    isPictureRequired: Boolean = true,
    isEditAllowed: Boolean = true,
    onDelete: (referenceId: String) -> Unit,
    onUpdate: (referenceId: String) -> Unit
) {


    val context = LocalContext.current

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = defaultCardElevation
        ),
        shape = RoundedCornerShape(roundedCornerRadiusDefault),
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .clickable {

            }
            .then(modifier)
    ) {

        val dividerHeight = remember {
            mutableStateOf(0.dp)
        }

        Column(
            modifier = Modifier
                .background(white)
                .padding(vertical = dimen_8_dp)
        ) {
            Spacer(modifier = Modifier.width(dimen_14_dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen_8_dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(dimen_14_dp))
                if (isPictureRequired) {
                    Box(modifier = Modifier.padding(start = 16.dp)) {
                        CircularImageViewComponent(
                            modifier = Modifier
                                .height(45.dp)
                                .width(45.dp),
                            imagePath = Uri.EMPTY
                        )
                    }
                }
                Spacer(modifier = Modifier.width(dimen_14_dp))
                Column(verticalArrangement = Arrangement.spacedBy(dimen_8_dp)) {
                    surveyConfig
                        .filter { it.value.type.equals(CONFIG_SLOT_TYPE_TAG, true) }
                        .forEach { mapEntry ->
                            val response =
                                getSavedAnswerValueForSummaryField(
                                    surveyAnswerFormSummaryUiModelList,
                                    mapEntry
                                )
                            SubContainerView(
                                mapEntry.value.copy(value = response),
                                isNumberFormattingRequired = false
                            )
                        }
                }
            }
            Spacer(modifier = Modifier.height(dimen_16_dp))
            Divider(
                thickness = dimen_1_dp,
                modifier = Modifier.fillMaxWidth(),
                color = borderGreyLight
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (surveyConfig.containsKey(SurveyConfigCardSlots.FORM_SUMMARY_CARD_EDIT_BUTTON.name)) {
                    TextButton(
                        onClick = {
                            if (isEditAllowed) {
                                onUpdate(referenceId)
                            } else {
                                showCustomToast(
                                    context,
                                    context.getString(R.string.edit_disable_message)
                                )
                            }
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = blueDark
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit Button",
                            tint = blueDark
                        )
                    }
                }
                if (surveyConfig.containsKey(SurveyConfigCardSlots.FORM_SUMMARY_CARD_DELETE_BUTTON.name)) {
                    Divider(
                        color = borderGreyLight,
                        modifier = Modifier
                            .fillMaxHeight()  //fill the max height
                            .width(1.dp)
                    )
                    TextButton(
                        onClick = {
                            if (isEditAllowed) {
                                onDelete(referenceId)
                            } else {
                                showCustomToast(
                                    context,
                                    context.getString(R.string.edit_disable_message)
                                )
                            }
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = blueDark
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete Button",
                            tint = blueDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getSavedAnswerValueForSummaryField(
    surveyAnswerFormSummaryUiModelList: List<SurveyAnswerFormSummaryUiModel>,
    mapEntry: Map.Entry<String, SurveyCardModel>
): String {
    var response = BLANK_STRING
    val optionsUiModelList =
        surveyAnswerFormSummaryUiModelList
            .find { it.tagId.contains(mapEntry.value.tagId) }?.optionItems
            ?.filter { it.isSelected == true }

    if (optionsUiModelList.isNullOrEmpty()) {
        return response
    }

    if (optionsUiModelList.size == 1) {
        val firstItem = optionsUiModelList.firstOrNull()
        if (QuestionType.singleResponseQuestionTypeQuestions.contains(
                firstItem?.optionType.value().toLowerCase()
            ) && !QuestionType.userInputQuestionTypeList.contains(
                firstItem?.optionType.value().toLowerCase()
            )
        ) {
            response = firstItem?.description.value()
        }

        if (QuestionType.userInputQuestionTypeList.contains(
                firstItem?.optionType.value().toLowerCase()
            ) && QuestionType.userInputQuestionTypeList.contains(
                firstItem?.optionType.value().toLowerCase()
            )
        ) {
            response = firstItem?.selectedValue.value()
        }
    } else {

        response = optionsUiModelList.map { it.description }.value().joinToString(PIPE_DELIMITER)

    }

    return response
}

