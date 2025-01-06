package com.sarathi.surveymanager.ui.component


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.toSize
import com.nudge.core.getQuestionNumber
import com.nudge.core.showCustomToast
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.theme.defaultCardElevation
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_2_dp
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.surveymanager.R

@Composable
fun TypeDropDownComponent(
    contests: List<ContentList?>? = listOf(),
    isFromTypeQuestion: Boolean = false,
    title: String = BLANK_STRING,
    hintText: String = stringResource(R.string.select),
    sources: List<ValuesDto>?,
    isMandatory: Boolean = false,
    isEditAllowed: Boolean = true,
    diableItem: Int = -1,
    showCardView: Boolean = false,
    questionNumber: String = BLANK_STRING,
    onDetailIconClicked: () -> Unit = {},
    navigateToMediaPlayerScreen: (ContentList) -> Unit,
    onAnswerSelection: (selectedValuesDto: ValuesDto) -> Unit
) {

    val context = LocalContext.current
    val defaultSourceList =
        sources ?: listOf(ValuesDto(id = 1, "Yes"), ValuesDto(id = 2, "No"))
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember {
        mutableStateOf(
            defaultSourceList.find { it.isSelected == true }?.value
                ?: hintText
        )
    }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    DropDownComponent(items = defaultSourceList,
        isFromTypeQuestion = isFromTypeQuestion,
        content = contests,
        showCardView = showCardView,
        modifier = Modifier.fillMaxWidth(),
        mTextFieldSize = textFieldSize,
        expanded = expanded,
        title = title,
        diableItem = diableItem,
        questionNumber = questionNumber,
        isMandatory = isMandatory,
        selectedItem = selectedOptionText,
        navigateToMediaPlayerScreen = { contentList ->
            navigateToMediaPlayerScreen(contentList)
        },
        onDetailIconClicked = { onDetailIconClicked() },
        onExpandedChange = {
            if (isEditAllowed) {
                expanded = !it
            } else {
                showCustomToast(
                    context,
                    context.getString(R.string.edit_disable_message)
                )
            }

        },
        onDismissRequest = {
            expanded = false
        },
        onGlobalPositioned = { coordinates ->
            textFieldSize = coordinates.size.toSize()
        },
        onItemSelected = {
            selectedOptionText =
                defaultSourceList[defaultSourceList.indexOf(it)].value
            onAnswerSelection(defaultSourceList[defaultSourceList.indexOf(it)])
            expanded = false

        }
    )


}

@Composable
fun TypeDropDownWithCardComponent(
    contests: List<ContentList?>? = listOf(),
    isFromTypeQuestion: Boolean = false,
    title: String = BLANK_STRING,
    hintText: String = stringResource(R.string.select),
    sources: List<ValuesDto>?,
    isMandatory: Boolean = false,
    isEditAllowed: Boolean = true,
    showCardView: Boolean = false,
    questionNumber: String = BLANK_STRING,
    onDetailIconClicked: () -> Unit = {},
    navigateToMediaPlayerScreen: (ContentList) -> Unit,
    onAnswerSelection: (selectedValuesDto: ValuesDto) -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(dimen_2_dp)
    ) {
        BasicCardView(
            cardElevation = CardDefaults.cardElevation(
                defaultElevation = defaultCardElevation
            ),
            cardShape = RoundedCornerShape(roundedCornerRadiusDefault),
            modifier = Modifier
                .fillMaxWidth()
                .background(white)
                .clickable {
                }
        ) {

            Box(
                modifier = Modifier
                    .padding(horizontal = dimen_16_dp, vertical = dimen_10_dp)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(roundedCornerRadiusDefault)
                    )
                    .clip(RoundedCornerShape(roundedCornerRadiusDefault))
            ) {
                TypeDropDownComponent(
                    isFromTypeQuestion = isFromTypeQuestion,
                    showCardView = showCardView,
                    contests = contests,
                    title = title,
                    hintText = hintText,
                    sources = sources,
                    isMandatory = isMandatory,
                    isEditAllowed = isEditAllowed,
                    questionNumber = questionNumber,
                    navigateToMediaPlayerScreen = { contentList ->
                        navigateToMediaPlayerScreen(contentList)
                    },
                    onDetailIconClicked = {
                        onDetailIconClicked()
                    },
                    onAnswerSelection = { selectedValuesDto ->
                        onAnswerSelection(selectedValuesDto)
                    }
                )
            }
        }
    }

}


@Composable
fun DropDownTypeComponent(
    contents: List<ContentList?>? = listOf(),
    questionIndex: Int,
    title: String = BLANK_STRING,
    hintText: String = stringResource(R.string.select),
    sources: List<ValuesDto>?,
    isMandatory: Boolean = false,
    showQuestionInCard: Boolean = false,
    isEditAllowed: Boolean = true,
    isFromTypeQuestion: Boolean = false,
    onDetailIconClicked: () -> Unit = {},
    navigateToMediaPlayerScreen: (ContentList) -> Unit,
    onAnswerSelection: (selectedValuesDto: ValuesDto) -> Unit
) {

    if (showQuestionInCard) {
        TypeDropDownWithCardComponent(
            onDetailIconClicked = { onDetailIconClicked() },
            contests = contents,
            showCardView = showQuestionInCard,
            title = title,
            hintText = hintText,
            sources = sources,
            isMandatory = isMandatory,
            isEditAllowed = isEditAllowed,
            questionNumber = getQuestionNumber(questionIndex),
            navigateToMediaPlayerScreen = { imageContent ->
                navigateToMediaPlayerScreen(imageContent)
            },
            onAnswerSelection = { selectedValuesDto ->
                onAnswerSelection(selectedValuesDto)
            }
        )
    } else {
        Box(
            modifier = Modifier
        ) {
            TypeDropDownComponent(
                title = title,
                isFromTypeQuestion = isFromTypeQuestion,
                contests = contents,
                hintText = hintText,
                sources = sources,
                isMandatory = isMandatory,
                isEditAllowed = isEditAllowed,
                questionNumber = BLANK_STRING,
                onDetailIconClicked = { onDetailIconClicked() },
                navigateToMediaPlayerScreen = { contentList ->
                    navigateToMediaPlayerScreen(contentList)
                },
                onAnswerSelection = { selectedValuesDto ->
                    onAnswerSelection(selectedValuesDto)
                }
            )
        }
    }

}