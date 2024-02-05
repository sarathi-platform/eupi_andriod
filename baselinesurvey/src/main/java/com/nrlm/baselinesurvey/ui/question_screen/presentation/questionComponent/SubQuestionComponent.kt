package com.nrlm.baselinesurvey.ui.question_screen.presentation.questionComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.database.entity.QuestionEntity
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import com.nrlm.baselinesurvey.model.response.QuestionList
import com.nrlm.baselinesurvey.ui.theme.dimen_16_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_18_dp
import com.nrlm.baselinesurvey.ui.theme.dimen_8_dp
import com.nrlm.baselinesurvey.ui.theme.white
import kotlinx.coroutines.launch

@Composable
fun SubQuestionComponent(
    modifier: Modifier = Modifier,
    outerState: LazyListState = rememberLazyListState(),
    innerState: LazyListState = rememberLazyListState(),
    queLazyState: LazyListState = rememberLazyListState(),
    maxCustomHeight: Dp,
    questionList: List<QuestionList?>?
) {
    val scope = rememberCoroutineScope()
    val outerState: LazyListState = rememberLazyListState()
    val innerState: LazyListState = rememberLazyListState()
    val tempHeight: Int = 170 * questionList?.size!!
    SideEffect {
        if (outerState.layoutInfo.visibleItemsInfo.size == 2 && innerState.layoutInfo.totalItemsCount == 0) scope.launch {
            outerState.scrollToItem(
                outerState.layoutInfo.totalItemsCount
            )
        }
        println("outer ${outerState.layoutInfo.visibleItemsInfo.map { it.index }}")
        println("inner ${innerState.layoutInfo.visibleItemsInfo.map { it.index }}")
    }
    BoxWithConstraints(
        modifier = modifier
            .scrollable(
                state = outerState,
                Orientation.Vertical,
            )
            .heightIn(min = 100.dp, maxCustomHeight)
    ) {
        Column(modifier = Modifier.background(white)) {
            Column(
                Modifier.padding(top = dimen_16_dp),
                verticalArrangement = Arrangement.spacedBy(dimen_18_dp)
            ) {
                LazyColumn(
                    state = innerState,
                    userScrollEnabled = false,
                    modifier = Modifier.height(tempHeight.dp),
                    verticalArrangement = Arrangement.spacedBy(dimen_8_dp)
                ) {
                    itemsIndexed(
                        items = questionList ?: emptyList()
                    ) { index, question ->
                        QuestionType(questionIndex = index,
                            question = getQuestion(question, 0, 1, 2),
                            optionItemEntityList = getOptionList(
                                question?.options,
                                question,
                                0,
                                1,
                                2
                            ),
                            maxCustomHeight = tempHeight.dp,
                            onAnswerSelection = { questionIndex, optionItem -> },
                            questionDetailExpanded = {},
                            onMediaTypeDescriptionAction = { descriptionContentType, contentLink -> })

                    }
                }
            }
        }
    }

}

private fun getQuestion(
    question: QuestionList?,
    sectionId: Int,
    surveyId: Int,
    languageId: Int
): QuestionEntity {
    return QuestionEntity(
        id = 0,
        questionId = question?.questionId,
        sectionId = sectionId,
        surveyId = surveyId,
        questionDisplay = question?.questionDisplay,
        questionSummary = question?.questionSummary,
        gotoQuestionId = question?.gotoQuestionId,
        order = question?.order,
        type = question?.type,
        languageId = languageId
    )
}

private fun getOptionList(
    optionList: List<OptionsItem?>?,
    question: QuestionList?,
    sectionId: Int,
    surveyId: Int,
    languageId: Int
): List<OptionItemEntity> {
    val optionItemEntityList = mutableListOf<OptionItemEntity>()
    optionList?.forEach { optionsItem ->
        val optionItemEntity = OptionItemEntity(
            id = 0,
            optionId = optionsItem?.optionId,
            questionId = question?.questionId,
            sectionId = sectionId,
            surveyId = surveyId,
            display = optionsItem?.display,
            weight = optionsItem?.weight,
            optionValue = optionsItem?.optionValue,
            summary = optionsItem?.summary,
            count = optionsItem?.count,
            optionImage = optionsItem?.optionImage,
            optionType = optionsItem?.optionType,
            questionList = optionsItem?.questionList,
            conditional = optionsItem?.conditional ?: false,
            order = optionsItem?.order ?: 0,
            values = optionsItem?.values,
            languageId = languageId
        )
        optionItemEntityList.add(optionItemEntity)
    }
    return optionItemEntityList
}