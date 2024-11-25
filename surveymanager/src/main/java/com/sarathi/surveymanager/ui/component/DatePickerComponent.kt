package com.sarathi.surveymanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nudge.core.DD_MMM_YYYY_FORMAT
import com.nudge.core.getQuestionNumber
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.commonUi.CustomDatePickerComponent
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.commonUi.rememberCustomDatePickerDialogProperties
import com.nudge.core.ui.commonUi.rememberCustomDatePickerState
import com.nudge.core.ui.commonUi.rememberDatePickerProperties
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.defaultCardElevation
import com.nudge.core.ui.theme.dimen_0_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_60_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.greyColor
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.placeholderGrey
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.smallerTextStyle
import com.nudge.core.ui.theme.white
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerComponent(
    isFromTypeQuestion: Boolean = false,
    onDetailIconClicked: () -> Unit = {}, // Default empty lambda
    contents: List<ContentList?>? = listOf(),
    questionIndex: Int,
    title: String = BLANK_STRING,
    hintText: String = BLANK_STRING,
    defaultValue: String = BLANK_STRING,
    showCardView: Boolean = false,
    isMandatory: Boolean = false,
    isEditable: Boolean = true,
    isFutureDateDisable: Boolean = false,
    navigateToMediaPlayerScreen: (ContentList) -> Unit,
    onAnswerSelection: (selectValue: String) -> Unit,
) {
    var text by remember { mutableStateOf(defaultValue) }


    val datePickerDialogProperties = rememberCustomDatePickerDialogProperties()

    val datePickerState =
        rememberCustomDatePickerState()

    val datePickerProperties = rememberDatePickerProperties(
        state = datePickerState,
        dateValidator = { selectedDate ->
            if (isFutureDateDisable) selectedDate <= System.currentTimeMillis() else true
        }
    )
    val scope = rememberCoroutineScope()
    val outerState: LazyListState = rememberLazyListState()
    val innerState: LazyGridState = rememberLazyGridState()
    SideEffect {
        if (outerState.layoutInfo.visibleItemsInfo.size == 2 && innerState.layoutInfo.totalItemsCount == 0)
            scope.launch { outerState.scrollToItem(outerState.layoutInfo.totalItemsCount) }
    }

    BasicCardView(
        cardElevation = CardDefaults.cardElevation(
            defaultElevation = if (showCardView) defaultCardElevation else dimen_0_dp
        ),
        cardShape = RoundedCornerShape(roundedCornerRadiusDefault),
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .clickable {

            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (showCardView) dimen_16_dp else dimen_0_dp)
        ) {
            if (title.isNotBlank()) {
                QuestionComponent(
                    isFromTypeQuestionInfoIconVisible = isFromTypeQuestion && contents?.isNotEmpty() == true,
                    onDetailIconClicked = { onDetailIconClicked() },
                    title = title,
                    questionNumber = if (showCardView) getQuestionNumber(questionIndex) else BLANK_STRING,
                    isRequiredField = isMandatory
                )
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                TextField(
                    value = text,
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimen_60_dp)
                        .background(white, shape = RoundedCornerShape(8.dp))
                        .border(1.dp, greyColor, shape = RoundedCornerShape(8.dp)),
                    onValueChange = { text = it },
                    textStyle = newMediumTextStyle.copy(blueDark),
                    placeholder = {
                        Text(
                            text = hintText,
                            style = smallerTextStyle.copy(
                                color = placeholderGrey
                            ),
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentHeight(align = Alignment.CenterVertically)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Calendar Icon",
                                tint = placeholderGrey
                            )
                        }
                    },
                    enabled = isEditable,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Transparent)
                        .clickable(enabled = isEditable) {
                            datePickerDialogProperties.show()

                        },
                )
                CustomDatePickerComponent(
                    datePickerProperties = datePickerProperties,
                    datePickerDialogProperties = datePickerDialogProperties,
                    onDismissRequest = {
                        datePickerDialogProperties.hide()
                    },
                    onConfirmButtonClicked = {
                        val dateFormat = SimpleDateFormat(DD_MMM_YYYY_FORMAT, Locale.ENGLISH)
                        val formattedDate =
                            dateFormat.format(datePickerState.selectedDateMillis)
                        text = formattedDate
                        onAnswerSelection(text)
                        datePickerDialogProperties.hide()
                    }
                )
            }
            if (showCardView) {
                CustomVerticalSpacer(size = dimen_6_dp)
                ContentBottomViewComponent(
                    contents = contents,
                    questionIndex = questionIndex,
                    showCardView = showCardView,
                    questionDetailExpanded = {},
                    navigateToMediaPlayerScreen = { content ->
                        navigateToMediaPlayerScreen(content)
                    }
                )
            }

        }
    }

}

