package com.sarathi.surveymanager.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.nudge.core.BLANK_STRING
import com.nudge.core.getQuestionNumber
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.borderGrey
import com.nudge.core.ui.theme.defaultCardElevation
import com.nudge.core.ui.theme.dimen_0_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_60_dp
import com.nudge.core.ui.theme.dimen_64_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.newMediumTextStyle
import com.nudge.core.ui.theme.placeholderGrey
import com.nudge.core.ui.theme.roundedCornerRadiusDefault
import com.nudge.core.ui.theme.white
import com.nudge.core.value
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
import com.sarathi.surveymanager.R
import kotlinx.coroutines.launch


@Composable
fun MultiSelectSelectDropDown(
    isFromTypeQuestion: Boolean = false,
    content: List<ContentList?>? = listOf(),
    questionIndex: Int,
    title: String = BLANK_STRING,
    isMandatory: Boolean = false,
    items: List<ValuesDto>,
    selectedItems: List<String>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxCustomHeight: Dp,
    hint: String = stringResource(R.string.select),
    expanded: Boolean = false,
    showCardView: Boolean = false,
    enabledOptions: Map<Int, Boolean?> = mapOf(),
    onDetailIconClicked: () -> Unit = {}, // Default empty lambda
    onExpandedChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    navigateToMediaPlayerScreen: (ContentList) -> Unit,
    onGlobalPositioned: (LayoutCoordinates) -> Unit,
    mTextFieldSize: Size,
) {
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    val scope = rememberCoroutineScope()
    val outerState: LazyListState = rememberLazyListState()
    val innerState: LazyGridState = rememberLazyGridState()
    SideEffect {
        if (outerState.layoutInfo.visibleItemsInfo.size == 2 && innerState.layoutInfo.totalItemsCount == 0)
            scope.launch { outerState.scrollToItem(outerState.layoutInfo.totalItemsCount) }
    }
    BoxWithConstraints(
        modifier = modifier
            .scrollable(
                state = outerState,
                Orientation.Vertical,
            )
            .heightIn(min = dimen_64_dp, maxCustomHeight)
            .background(white)
            .padding(horizontal = if (showCardView) dimen_16_dp else dimen_0_dp)
    ) {
        BasicCardView(
            cardElevation = CardDefaults.cardElevation(
                defaultElevation = if (showCardView) defaultCardElevation else dimen_0_dp
            ),
            cardShape = RoundedCornerShape(roundedCornerRadiusDefault),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight, max = maxHeight)
                .then(modifier)
        ) {
            Column(
                modifier = modifier
                    .background(white),
                horizontalAlignment = Alignment.Start
            ) {

                val txt = if (selectedItems.isNotEmpty()) {
                    selectedItems.joinToString(", ")
                } else {
                    stringResource(R.string.select)
                }
                if (title.isNotBlank()) {
                    QuestionComponent(
                        isFromTypeQuestionInfoIconVisible = isFromTypeQuestion && content?.isNotEmpty() == true,
                        title = title,
                        questionNumber = if (showCardView) getQuestionNumber(questionIndex) else BLANK_STRING,
                        isRequiredField = isMandatory,
                        onDetailIconClicked = { onDetailIconClicked() }
                    )
                }
                CustomOutlineTextField(
                    value = txt,
                    onValueChange = {
                    },
                    interactionSource = remember { MutableInteractionSource() }
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is PressInteraction.Release) {
                                        onExpandedChange(expanded)
                                    }
                                }
                            }
                        },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimen_60_dp)
                        .background(white)
                        .clickable { onExpandedChange(expanded) }
                        .onGloballyPositioned { coordinates ->
                            onGlobalPositioned(coordinates)
                        },
                    textStyle = newMediumTextStyle.copy(blueDark),
                    singleLine = true,
                    maxLines = 1,
                    placeholder = {
                        Text(text = hint, style = newMediumTextStyle, color = placeholderGrey)
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = blueDark,
                        backgroundColor = Color.White,
                        focusedIndicatorColor = borderGrey,
                        unfocusedIndicatorColor = borderGrey,
                    ),
                    trailingIcon = {
                        Icon(icon, "contentDescription",
                            Modifier.clickable { onExpandedChange(expanded) })
                    }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { onDismissRequest() },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
                        .background(
                            white
                        )
                ) {

                    items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedItems.contains(item.value),
                                onCheckedChange = {
                                    onItemSelected(item.value)
                                },
                                enabled = enabledOptions[item.id].value(true),
                                colors = CheckboxDefaults.colors(
                                    checkedColor = blueDark,
                                    uncheckedColor = Color.Gray,
                                    checkmarkColor = Color.White
                                ),
                            )
                            Text(
                                text = item.value,
                                style = newMediumTextStyle,
                                textAlign = TextAlign.Start,
                                color = getDropDownOptionTextColor(
                                    item.value,
                                    selectedItems,
                                    enabledOptions[item.id].value(true)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (enabledOptions[item.id].value(true))
                                            onItemSelected(item.value.toString())
                                    }
                            )
                        }
                    }
                }

                if (showCardView && content?.isNotEmpty() == true) {
                    CustomVerticalSpacer(size = dimen_6_dp)
                    ContentBottomViewComponent(
                        contents = content,
                        questionIndex = questionIndex,
                        showCardView = showCardView,
                        questionDetailExpanded = {},
                        navigateToMediaPlayerScreen = { contentList ->
                            navigateToMediaPlayerScreen(contentList)
                        }
                    )
                }
            }
        }
    }
}

fun getDropDownOptionTextColor(
    value: String,
    selectedItems: List<String>,
    isEnabled: Boolean
): Color {
    val alpha = if (isEnabled) 1f else 0.5f
    return if (selectedItems.contains(value)) blueDark.copy(alpha = alpha) else blueDark.copy(alpha)
}
