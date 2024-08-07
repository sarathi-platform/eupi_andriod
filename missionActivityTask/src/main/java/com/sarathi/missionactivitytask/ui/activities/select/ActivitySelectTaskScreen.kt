package com.sarathi.missionactivitytask.ui.activities.select

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nudge.core.BLANK_STRING
import com.nudge.core.EXPANSTION_TRANSITION_DURATION
import com.nudge.core.isOnline
import com.nudge.core.ui.commonUi.BasicCardView
import com.nudge.core.ui.commonUi.CardArrow
import com.nudge.core.ui.commonUi.CustomVerticalSpacer
import com.nudge.core.ui.theme.NotoSans
import com.nudge.core.ui.theme.blueDark
import com.nudge.core.ui.theme.buttonTextStyle
import com.nudge.core.ui.theme.dimen_10_dp
import com.nudge.core.ui.theme.dimen_16_dp
import com.nudge.core.ui.theme.dimen_1_dp
import com.nudge.core.ui.theme.dimen_3_dp
import com.nudge.core.ui.theme.dimen_5_dp
import com.nudge.core.ui.theme.dimen_6_dp
import com.nudge.core.ui.theme.dimen_72_dp
import com.nudge.core.ui.theme.dimen_8_dp
import com.nudge.core.ui.theme.greenOnline
import com.nudge.core.ui.theme.languageItemActiveBg
import com.nudge.core.ui.theme.textColorDark
import com.nudge.core.ui.theme.white
import com.sarathi.contentmodule.ui.content_screen.screen.BaseContentScreen
import com.sarathi.dataloadingmangement.data.entities.OptionItemEntity
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardModel
import com.sarathi.dataloadingmangement.model.uiModel.TaskCardSlots
import com.sarathi.dataloadingmangement.util.constants.QuestionType
import com.sarathi.missionactivitytask.R
import com.sarathi.missionactivitytask.navigation.navigateToActivityCompletionScreen
import com.sarathi.missionactivitytask.navigation.navigateToContentDetailScreen
import com.sarathi.missionactivitytask.navigation.navigateToMediaPlayerScreen
import com.sarathi.missionactivitytask.ui.basic_content.component.ImageViewer
import com.sarathi.missionactivitytask.ui.basic_content.component.SubContainerView
import com.sarathi.missionactivitytask.ui.components.CircularImageViewComponent
import com.sarathi.missionactivitytask.ui.components.ToolBarWithMenuComponent
import com.sarathi.missionactivitytask.utils.StatusEnum
import com.sarathi.missionactivitytask.utils.event.InitDataEvent
import com.sarathi.missionactivitytask.utils.event.LoaderEvent
import com.sarathi.surveymanager.ui.component.ButtonPositive
import java.io.File

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActivitySelectTaskScreen(
    navController: NavController = rememberNavController(),
    viewModel: ActivitySelectTaskViewModel = hiltViewModel(),
    missionId: Int,
    activityName: String,
    activityId: Int,
    onSettingClick: () -> Unit
) {

    val context = LocalContext.current
    val pullRefreshState = rememberPullRefreshState(
        viewModel.loaderState.value.isLoaderVisible,
        {
            if (isOnline(context)) {
                viewModel.refreshData()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.refresh_failed_please_try_again),
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    LaunchedEffect(key1 = true) {
        viewModel.setMissionActivityId(missionId, activityId)
        viewModel.onEvent(InitDataEvent.InitTaskScreenState(emptyList()))
        viewModel.onEvent(LoaderEvent.UpdateLoaderState(false))

    }
    ToolBarWithMenuComponent(
        title = activityName,
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        onBackIconClick = { navController.popBackStack() },
        isSearch = true,
        onSearchValueChange = { _ ->

        },
        onRetry = {},
        onBottomUI = {
            BottomAppBar(
                modifier = Modifier.height(dimen_72_dp),
                backgroundColor = white
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimen_10_dp),
                ) {

                    ButtonPositive(
                        modifier = Modifier.weight(0.5f),
                        buttonTitle = stringResource(R.string.complete_activity),
                        isActive = viewModel.isButtonEnable.value,
                        isArrowRequired = false,
                        onClick = {
                            viewModel.markActivityCompleteStatus()

                            navigateToActivityCompletionScreen(
                                isFromActivity = true,
                                navController = navController,
                                activityMsg = context.getString(
                                    R.string.activity_completion_message,
                                    activityName
                                )
                            )
                        })

                }
            }
        },
        onContentUI = { _, isSearch, _ ->

            Column {
                BaseContentScreen(
                    matId = viewModel.matId.value,
                    contentScreenCategory = viewModel.contentCategory.value
                ) { _, contentKey, contentType, isLimitContentData, contentTitle ->
                    if (!isLimitContentData) {
                        navigateToMediaPlayerScreen(
                            navController = navController,
                            contentKey = contentKey,
                            contentType = contentType,
                            contentTitle = contentTitle,
                        )
                    } else {
                        navigateToContentDetailScreen(
                            navController,
                            matId = viewModel.matId.value,
                            contentScreenCategory = viewModel.contentCategory.value
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState)
                )
                {
                    PullRefreshIndicator(
                        refreshing = viewModel.loaderState.value.isLoaderVisible,
                        state = pullRefreshState,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .zIndex(1f),
                        contentColor = blueDark,
                    )
                    Spacer(modifier = Modifier.height(dimen_10_dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimen_16_dp)
                    ) {
                        viewModel.filterTaskMap.forEach { (category, itemsInCategory) ->
                            itemsIndexed(
                                items = itemsInCategory
                            ) { _, task ->
                                val options = TaskCardModel(
                                    label = "option1, option2 ",
                                    value = "subTitle 1",
                                    icon = null
                                )
                                val optionitemList = listOf(
                                    OptionItemEntity(
                                        id = 1,
                                        sectionId = 5,
                                        originalValue = "Yes",
                                        selectedValue = BLANK_STRING,
                                        conditions = emptyList(),
                                        isSelected = false,
                                        optionType = QuestionType.MultiSelect.name,
                                        questionId = 22,
                                        conditional = false,
                                        optionImage = BLANK_STRING,
                                        optionValue = null,
                                        contentEntities = emptyList(),
                                        selectedValueId = 0,
                                        count = 0,
                                        order = 1,
                                        userId = "Ultra Poor change maker (UPCM)_6666667777",
                                        values = null,
                                        weight = 0,
                                        optionId = 9,
                                        surveyId = 1,
                                        summary = BLANK_STRING
                                    ),

                                    OptionItemEntity(
                                        id = 2,
                                        sectionId = 5,
                                        originalValue = "No",
                                        selectedValue = BLANK_STRING,
                                        conditions = emptyList(),
                                        isSelected = false,
                                        optionType = QuestionType.MultiSelect.name,
                                        questionId = 22,
                                        conditional = false,
                                        optionImage = BLANK_STRING,
                                        optionValue = null,
                                        contentEntities = emptyList(),
                                        selectedValueId = 0,
                                        count = 0,
                                        order = 2,
                                        userId = "Ultra Poor change maker (UPCM)_6666667777",
                                        values = null,
                                        weight = 0,
                                        optionId = 10,
                                        surveyId = 1,
                                        summary = BLANK_STRING
                                    ),

                                    OptionItemEntity(
                                        id = 3,
                                        sectionId = 5,
                                        originalValue = "Not Available",
                                        selectedValue = BLANK_STRING,
                                        conditions = emptyList(),
                                        isSelected = false,
                                        optionType = QuestionType.MultiSelect.name,
                                        questionId = 22,
                                        conditional = false,
                                        optionImage = BLANK_STRING,
                                        optionValue = null,
                                        contentEntities = emptyList(),
                                        selectedValueId = 0,
                                        count = 0,
                                        order = 3,
                                        userId = "Ultra Poor change maker (UPCM)_6666667777",
                                        values = null,
                                        weight = 0,
                                        optionId = 11,
                                        surveyId = 1,
                                        summary = BLANK_STRING
                                    )

                                )
                                ExpandableTaskCard(
                                    title = task.value[TaskCardSlots.TASK_TITLE.name],
                                    subTitle1 = task.value[TaskCardSlots.TASK_SUBTITLE.name],
                                    options = options,
                                    status = task.value[TaskCardSlots.TASK_STATUS.name],
                                    imagePath = Uri.EMPTY,
                                    modifier = Modifier,
                                    optionItemEntityList = optionitemList,
                                    expanded = true,
                                    onExpendClick = { _, _ ->

                                    }
                                )

                                CustomVerticalSpacer()
                            }
                        }

                    }
                }
            }
        },
        onSettingClick = onSettingClick
    )
}


@Preview(showBackground = true)
@Composable
fun ExpandableTaskCardPreview() {
    val title = TaskCardModel(label = "Title1", value = "Title 1", icon = null)
    val subTitle1 = TaskCardModel(label = "subTitle1", value = "subTitle 1", icon = null)
    val options = TaskCardModel(label = "option1, option2 ", value = "subTitle 1", icon = null)
    val status = TaskCardModel(label = "Status 1", value = StatusEnum.NOT_STARTED.name, icon = null)
    val optionitemList = listOf(
        OptionItemEntity(
            id = 1,
            sectionId = 5,
            originalValue = "Yes",
            selectedValue = BLANK_STRING,
            conditions = emptyList(),
            isSelected = false,
            optionType = QuestionType.MultiSelect.name,
            questionId = 22,
            conditional = false,
            optionImage = BLANK_STRING,
            optionValue = null,
            contentEntities = emptyList(),
            selectedValueId = 0,
            count = 0,
            order = 1,
            userId = "Ultra Poor change maker (UPCM)_6666667777",
            values = null,
            weight = 0,
            optionId = 9,
            surveyId = 1,
            summary = BLANK_STRING
        ),

        OptionItemEntity(
            id = 2,
            sectionId = 5,
            originalValue = "No",
            selectedValue = BLANK_STRING,
            conditions = emptyList(),
            isSelected = false,
            optionType = QuestionType.MultiSelect.name,
            questionId = 22,
            conditional = false,
            optionImage = BLANK_STRING,
            optionValue = null,
            contentEntities = emptyList(),
            selectedValueId = 0,
            count = 0,
            order = 2,
            userId = "Ultra Poor change maker (UPCM)_6666667777",
            values = null,
            weight = 0,
            optionId = 10,
            surveyId = 1,
            summary = BLANK_STRING
        ),

        OptionItemEntity(
            id = 3,
            sectionId = 5,
            originalValue = "Not Available",
            selectedValue = BLANK_STRING,
            conditions = emptyList(),
            isSelected = false,
            optionType = QuestionType.MultiSelect.name,
            questionId = 22,
            conditional = false,
            optionImage = BLANK_STRING,
            optionValue = null,
            contentEntities = emptyList(),
            selectedValueId = 0,
            count = 0,
            order = 3,
            userId = "Ultra Poor change maker (UPCM)_6666667777",
            values = null,
            weight = 0,
            optionId = 11,
            surveyId = 1,
            summary = BLANK_STRING
        )

    )
    ExpandableTaskCard(
        title = title,
        status = status,
        subTitle1 = subTitle1,
        modifier = Modifier,
        imagePath = File("").toUri(),
        expanded = true,
        options = options,
        optionItemEntityList = optionitemList,
        onExpendClick = { _, _ -> }
    )
}

@Composable
fun ExpandableTaskCard(
    title: TaskCardModel?,
    subTitle1: TaskCardModel?,
    options: TaskCardModel?,
    status: TaskCardModel?,
    imagePath: Uri,
    modifier: Modifier,
    optionItemEntityList: List<OptionItemEntity>,
    expanded: Boolean,
    onExpendClick: (Boolean, TaskCardModel) -> Unit,
) {
    val taskStatus = remember(status?.value) {
        mutableStateOf(status?.value)
    }

    val transition = updateTransition(expanded, label = "transition")


    val animateColor by transition.animateColor({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "animate color") {
        if (it) {
            greenOnline
        } else {
            textColorDark
        }
    }


    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPANSTION_TRANSITION_DURATION)
    }, label = "rotationDegreeTransition") {
        if (it) 180f else 0f
    }
    BasicCardView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimen_16_dp)
            .border(
                width = dimen_1_dp,
                color = greenOnline,
                shape = RoundedCornerShape(dimen_6_dp)
            )
            .background(Color.Transparent)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(white)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimen_16_dp, vertical = dimen_5_dp),
                horizontalArrangement = Arrangement.spacedBy(dimen_10_dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (imagePath != null) {
                    CircularImageViewComponent(modifier = Modifier, imagePath = imagePath)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        title?.value?.let {
                            if (it.contains("vo", true)) {
                                title.icon?.let {
                                    ImageViewer(it)
                                    Spacer(modifier = Modifier.width(dimen_5_dp))
                                }
                                Spacer(modifier = Modifier.width(dimen_3_dp))
                            }
                            Text(
                                text = title.value,
                                style = buttonTextStyle,
                                color = blueDark
                            )
                        }
                    }
                    SubContainerView(subTitle1)
                }

                CardArrow(
                    modifier = Modifier,
                    degrees = arrowRotationDegree,
                    iconColor = animateColor,
                    arrowIcon = R.drawable.ic_baseline_keyboard_arrow_down_24,
                    onClick = {
                        if (title != null) {
                            onExpendClick(expanded, title)
                        }
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            LazyVerticalGrid(
                userScrollEnabled = false,
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = dimen_16_dp)
                    .heightIn(
                        min = 110.dp,
                        max = 140.dp
                    ),
                horizontalArrangement = Arrangement.Center
            ) {
                itemsIndexed(optionItemEntityList.sortedBy { it.optionId }
                    ?: emptyList()) { _index, optionItem ->
                    if (optionItem.optionType?.equals(
                            QuestionType.MultiSelect.name,
                            true
                        ) == true
                    ) {
                        GridOptionCard(
                            optionItem = optionItem,
                            index = _index,
                            isEnabled = true,
                            selectedIndex = emptyList()
                        ) { _ ->

                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

            }
        }

        if (taskStatus?.value == StatusEnum.INPROGRESS.name) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimen_5_dp)
            ) {
                Divider(
                    modifier = Modifier
                        .height(dimen_1_dp)
                        .weight(1f)

                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimen_16_dp,
                        end = dimen_16_dp,
                        bottom = dimen_8_dp,
                        top = dimen_8_dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(dimen_10_dp)
            ) {
                SubContainerView(options)
            }
        }
    }
}

@Composable
fun GridOptionCard(
    modifier: Modifier = Modifier,
    optionItem: OptionItemEntity,
    index: Int,
    isEnabled: Boolean = true,
    selectedIndex: List<Int>,
    onOptionSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimen_5_dp, vertical = dimen_5_dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (selectedIndex.contains(optionItem.optionId)) blueDark else languageItemActiveBg)
            .clickable {
                onOptionSelected(optionItem.optionId ?: -1)
            }
            .then(modifier)) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = optionItem.originalValue.toString() ?: BLANK_STRING,
                    style = TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    ),
                    color = if (selectedIndex.contains(optionItem.optionId)) white else textColorDark.copy(
                        alpha = if (isEnabled) 1f else 0.5f
                    )
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
        )
    }

}